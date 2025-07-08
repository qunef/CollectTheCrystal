package presenter;

import model.Ball;
import model.Basket;
import model.DatabaseConnection;
import model.Lasso;
import model.Player;
import view.GameView;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.sql.*; 
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Kelas GamePresenter adalah inti dari logika permainan.
 * Bertanggung jawab untuk mengelola state (Model), menerima input dari (View),
 * dan menjalankan game loop dalam sebuah Thread terpisah.
 */
public class GamePresenter implements Runnable {

    // Komponen dari arsitektur MVP
    private final GameView view;
    private final Player player;
    private final Basket basket;
    private final List<Ball> balls;
    private final Lasso lasso;

    // State permainan
    private int score;
    private int count;
    private String currentUsername;

    // Kontrol Thread dan game loop
    private volatile boolean isRunning = false;
    private Thread gameThread;

    // Kontrol pergerakan pemain
    private boolean moveUp, moveDown, moveLeft, moveRight;

    public GamePresenter(GameView view) {
        this.view = view;
        this.player = new Player(375, 275); // Pemeran utama game muncul dari tengah. 
        this.basket = new Basket(740, 280, 30, 80);
        this.balls = new CopyOnWriteArrayList<>(); // Gunakan list yang thread-safe
        this.lasso = new Lasso(player);
    }

    /**
     * Memulai sesi permainan baru.
     * Menerima username dari menu utama, mereset state, dan memulai game thread.
     * @param username Nama pemain untuk sesi ini.
     */
    public void startGame(String username) {
        // Mencegah game loop ganda
        if (gameThread != null && gameThread.isAlive()) return;

        this.currentUsername = username;

        // Reset state permainan
        score = 0;
        count = 0;
        balls.clear();
        player.setPosition(375, 275);
        lasso.resetAfterRetraction();

        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Menghentikan game loop dengan aman.
     */
    public void stopGame() {
        isRunning = false;
        gameThread = null;
    }

    /**
     * Metode utama yang dijalankan oleh Thread. Berisi game loop.
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ticksPerSecond = 60.0;
        final double ns = 1000000000 / ticksPerSecond;
        double delta = 0;
        long ballSpawnTimer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                updateGameLogic();
                delta--;
            }

            // Perintahkan View untuk menggambar ulang (render) dari game thread.
            // Harus menggunakan invokeLater agar aman untuk Swing.
            SwingUtilities.invokeLater(() -> {
                view.setGameElements(player, balls, lasso, basket);
                view.setScoreAndCount(score, count);
                view.updateDisplay();
            });

            // Munculkan bola baru setiap 2 detik
            if (System.currentTimeMillis() - ballSpawnTimer > 2000) {
                spawnBall();
                ballSpawnTimer = System.currentTimeMillis();
            }

            // Jeda singkat untuk mencegah penggunaan CPU 100%
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }


    private void updateGameLogic() {
        // 1. Pergerakan dasar pemain dan tali lasso
        player.move(moveUp, moveDown, moveLeft, moveRight);
        lasso.update();

        // 2. Logika untuk bola-bola yang masih bebas
        for (Ball ball : balls) {
            ball.move();
            // Cek jika lasso mengenai bola yang bebas
            if (lasso.isExtending() && !ball.isCaught() && player.getHeldBall() == null) {
                if (lasso.intersects(ball)) {
                    lasso.catchBall(ball);
                    break;
                }
            }
        }

        // 3. Logika Tali Lasso & Menangkap Bola
        // Jika tali sudah kembali ke pemain sambil membawa bola...
        if (lasso.hasReturnedToPlayer()) {
            Ball caughtBall = lasso.getRetractedBall();
            balls.remove(caughtBall); // Hapus bola dari daftar bola bebas
            player.holdBall(caughtBall); // Serahkan bola untuk dipegang pemain
            lasso.resetAfterRetraction(); // Reset tali lasso
        }

        // 4. Logika Bola yang Dipegang Pemain
        // Jika pemain sedang memegang bola, buat posisi bola mengikuti pemain
        if (player.getHeldBall() != null) {
            Ball currentHeldBall = player.getHeldBall();
            // Posisikan bola sedikit di atas kepala pemain
            currentHeldBall.setPosition(player.getX() + 30, player.getY() + 5);
        }
        
        // 5. Logika Mencetak Skor (Tabrakan KRISTAL & Keranjang) - INI PERUBAHANNYA
        // Jika pemain memegang bola DAN bola tersebut menyentuh keranjang...
        Ball heldBall = player.getHeldBall();
        if (heldBall != null && heldBall.getBounds().intersects(basket.getBounds())) {
            score += heldBall.getScore(); // Tambah skor
            count++; // Tambah hitungan
            player.releaseBall(); // Lepaskan bola (bola hilang dan skor tercatat)
        }
    }

    /**
     * Membuat bola baru dengan posisi dan skor acak.
     */
    private void spawnBall() {
        Random rand = new Random();
        
        // Tentukan tipe bola secara acak (logika ini tetap sama)
        int chance = rand.nextInt(100);
        Ball.BallType randomType;
        if (chance < 40) randomType = Ball.BallType.RED;
        else if (chance < 70) randomType = Ball.BallType.BLUE;
        else if (chance < 95) randomType = Ball.BallType.GREEN;
        else randomType = Ball.BallType.PURPLE;

        // --- LOGIKA BARU UNTUK MENGHINDARI KERANJANG ---

        // 1. Tentukan "zona terlarang" berdasarkan posisi dan ukuran keranjang
        // Kita tambahkan 'padding' agar ada jarak aman antara bola dan keranjang
        int padding = 50; // Jarak aman (bisa diubah)
        int basketTopY = basket.getBounds().y - padding;
        int basketBottomY = basket.getBounds().y + basket.getBounds().height + padding;

        // 2. Tentukan area spawn yang valid
        int topZoneHeight = basketTopY;
        int bottomZoneStart = basketBottomY;
        // Asumsi tinggi game panel adalah 600, dan kita batasi spawn hingga y=550
        int bottomZoneHeight = 550 - bottomZoneStart; 
        
        int yPos;

        // 3. Pilih secara acak mau spawn di zona atas atau bawah
        if (rand.nextBoolean()) {
            // Muncul di zona atas
            // Hasilkan angka acak dari 0 hingga setinggi zona atas
            yPos = rand.nextInt(topZoneHeight);
        } else {
            // Muncul di zona bawah
            // Hasilkan angka acak dari 0 hingga setinggi zona bawah, lalu geser ke bawah
            yPos = rand.nextInt(bottomZoneHeight) + bottomZoneStart;
        }

        // --- AKHIR LOGIKA BARU ---

        // Logika arah bola (kanan-kiri atau kiri-kanan) tetap sama
        int middleY = 300;
        if (yPos < middleY) {
            // Kanan ke Kiri (atas)
            balls.add(new Ball(800, yPos, -2, randomType));
        } else {
            // Kiri ke Kanan (bawah)
            balls.add(new Ball(0, yPos, 2, randomType));
        }
    }

    /**
     * Menyimpan skor akhir ke database MySQL.
     * Jika username sudah ada, skor akan diakumulasikan. Jika tidak, data baru akan dibuat.
     */
    private void saveCurrentScore() {
        if (score == 0) return; // Jangan simpan jika tidak ada skor

        String usernameToSave = this.currentUsername;
        if (usernameToSave == null || usernameToSave.trim().isEmpty()) {
            usernameToSave = "Player"; // Username default
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Mematikan auto-commit untuk manajemen transaksi manual
            conn.setAutoCommit(false);

            // 1. Cek apakah username sudah ada dan ambil data lamanya
            String checkSql = "SELECT skor, count FROM thasil WHERE username = ?";
            int oldSkor = 0;
            int oldCount = 0;
            boolean userExists = false;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, usernameToSave);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Jika user ditemukan, simpan data lamanya
                        userExists = true;
                        oldSkor = rs.getInt("skor");
                        oldCount = rs.getInt("count");
                    }
                }
            }

            // 2. Putuskan untuk UPDATE (jika ada) atau INSERT (jika tidak ada)
            if (userExists) {
                // Pengguna sudah ada, lakukan UPDATE dengan menambahkan skor baru
                String updateSql = "UPDATE thasil SET skor = ?, count = ? WHERE username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, oldSkor + this.score);
                    updateStmt.setInt(2, oldCount + this.count);
                    updateStmt.setString(3, usernameToSave);
                    updateStmt.executeUpdate();
                }
            } else {
                // Pengguna baru, lakukan INSERT seperti biasa
                String insertSql = "INSERT INTO thasil(username, skor, count) VALUES(?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, usernameToSave);
                    insertStmt.setInt(2, this.score);
                    insertStmt.setInt(3, this.count);
                    insertStmt.executeUpdate();
                }
            }

            // Jika semua operasi berhasil, commit transaksi
            conn.commit();

        } catch (SQLException e) {
            // Jika terjadi error, batalkan semua perubahan dalam transaksi ini
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "Gagal menyimpan skor ke database:\n" + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Selalu pastikan koneksi ditutup pada akhirnya
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan ke mode auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // --- KONTROL INPUT DARI VIEW ---

    /**
     * Dipanggil oleh View saat tombol keyboard ditekan.
     * @param keyCode Kode tombol yang ditekan.
     */
    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP: moveUp = true; break;
            case KeyEvent.VK_DOWN: moveDown = true; break;
            case KeyEvent.VK_LEFT: 
                moveLeft = true; 
                player.setFacingDirection(Player.Direction.LEFT); // TAMBAHKAN BARIS INI
                break;
            case KeyEvent.VK_RIGHT: 
                moveRight = true; 
                player.setFacingDirection(Player.Direction.RIGHT); // TAMBAHKAN BARIS INI
                break;
            case KeyEvent.VK_SPACE:
                stopGame();
                saveCurrentScore();
                view.showMainMenu();
                break;
        }
    }

    /**
     * Dipanggil oleh View saat tombol keyboard dilepas.
     * @param keyCode Kode tombol yang dilepas.
     */
    public void onKeyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP: moveUp = false; break;
            case KeyEvent.VK_DOWN: moveDown = false; break;
            case KeyEvent.VK_LEFT: moveLeft = false; break;
            case KeyEvent.VK_RIGHT: moveRight = false; break;
        }
    }

    /**
     * Dipanggil oleh View saat mouse ditekan.
     * @param x Posisi x mouse.
     * @param y Posisi y mouse.
     */
    public void onMousePressed(int x, int y) {
        // Tali lasso dapat memendek dan memanjang sesuai klik dari layar. 
        if (!lasso.isActive()) {
            lasso.activate(x, y);
        }
    }
}