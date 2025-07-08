package view;

import model.Ball;
import model.Basket;
import model.Lasso;
import model.Player;
import presenter.GamePresenter;
import util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel adalah komponen View utama yang bertanggung jawab untuk menampilkan
 * semua elemen visual dari permainan.
 */
public class GamePanel extends JPanel implements GameView, KeyListener {

    private final GamePresenter presenter;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // Variabel untuk menyimpan data yang akan digambar, diterima dari Presenter
    private Player playerToDraw;
    private Basket basketToDraw;
    private Lasso lassoToDraw;
    private List<Ball> ballsToDraw = new ArrayList<>();
    private int score, count;

    public GamePanel(JPanel mainPanel, CardLayout cardLayout) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.presenter = new GamePresenter(this);

        setFocusable(true);
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                presenter.onMousePressed(e.getX(), e.getY());
            }
        });
    }

    public GamePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Mengaktifkan anti-aliasing agar gambar dan teks lebih halus
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Gambar Latar Belakang
        if (AssetLoader.backgroundSprite != null) {
            g2d.drawImage(AssetLoader.backgroundSprite, 0, 0, getWidth(), getHeight(), null);
        }

        // 2. Gambar Keranjang (Palka Kargo)
        if (basketToDraw != null && AssetLoader.cargoBaySprite != null) {
            int drawX = basketToDraw.getX() - (AssetLoader.cargoBaySprite.getWidth() / 2);
            int drawY = basketToDraw.getY() - (AssetLoader.cargoBaySprite.getHeight() / 2);
            g2d.drawImage(AssetLoader.cargoBaySprite, drawX, drawY, null);
        }

        // 3. Gambar Bola (Kristal) yang bebas
        if (ballsToDraw != null) {
            for (Ball ball : ballsToDraw) {
                drawBallWithScore(g2d, ball);
            }
        }

        // 4. Gambar Tali Lasso (Tractor Beam)
        if (lassoToDraw != null) {
            lassoToDraw.draw(g2d);
        }

        // 5. Gambar Pemeran Utama (Astronot)
        if (playerToDraw != null && AssetLoader.playerSprite != null) {
            BufferedImage playerImage = AssetLoader.playerSprite;
            Player.Direction facing = playerToDraw.getFacingDirection();

            int drawX = playerToDraw.getX() - (playerImage.getWidth() / 2);
            int drawY = playerToDraw.getY() - (playerImage.getHeight() / 2);

            if (facing == Player.Direction.LEFT) {
                // Gambar dibalik secara horizontal jika menghadap kiri
                g2d.drawImage(playerImage, drawX + playerImage.getWidth(), drawY, -playerImage.getWidth(), playerImage.getHeight(), null);
            } else {
                // Gambar normal jika menghadap kanan
                g2d.drawImage(playerImage, drawX, drawY, null);
            }
        }

        // 6. Gambar Bola yang sedang dipegang pemain
        if (playerToDraw != null && playerToDraw.getHeldBall() != null) {
            drawBallWithScore(g2d, playerToDraw.getHeldBall());
        }

        // 7. Gambar Teks Skor dan Hitungan
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Count: " + count, 20, 55);

        /*
        // --- KODE DEBUGGING UNTUK MENGGAMBAR HITBOX ---
        g2d.setColor(Color.RED);
        if (playerToDraw != null) g2d.draw(playerToDraw.getBounds());
        if (basketToDraw != null) g2d.draw(basketToDraw.getBounds());
        if (ballsToDraw != null) { for (Ball ball : ballsToDraw) g2d.draw(ball.getBounds()); }
        if (playerToDraw != null && playerToDraw.getHeldBall() != null) { g2d.draw(playerToDraw.getHeldBall().getBounds());}
        */
    }
    
    /**
     * Fungsi pembantu untuk menggambar satu bola/kristal beserta teks skornya.
     * @param g2d Objek Graphics2D untuk menggambar.
     * @param ball Bola yang akan digambar.
     */
    private void drawBallWithScore(Graphics2D g2d, Ball ball) {
        BufferedImage crystalSprite = null;
        // Pilih gambar sprite berdasarkan tipe bola
        switch (ball.getType()) {
            case RED:    crystalSprite = AssetLoader.crystalRedSprite;    break;
            case BLUE:   crystalSprite = AssetLoader.crystalBlueSprite;   break;
            case GREEN:  crystalSprite = AssetLoader.crystalGreenSprite;  break;
            case PURPLE: crystalSprite = AssetLoader.crystalPurpleSprite; break;
        }

        if (crystalSprite != null) {
            int drawX = ball.getX() - (crystalSprite.getWidth() / 2);
            int drawY = ball.getY() - (crystalSprite.getHeight() / 2);
            g2d.drawImage(crystalSprite, drawX, drawY, null);

            // Gambar Teks Skor di atas bola
            String scoreText = String.valueOf(ball.getScore());
            Font scoreFont = new Font("Consolas", Font.BOLD, 14);
            g2d.setFont(scoreFont);
            
            // Dapatkan ukuran teks untuk memposisikannya di tengah
            FontMetrics metrics = g2d.getFontMetrics(scoreFont);
            int textX = ball.getX() - metrics.stringWidth(scoreText) / 2;
            int textY = drawY - 5; // 5 piksel di atas gambar

            // Efek "Neon" sederhana dengan menggambar bayangan/glow
            g2d.setColor(new Color(255, 255, 255, 100)); // Putih transparan
            g2d.drawString(scoreText, textX + 1, textY + 1);

            // Teks utama yang tajam
            g2d.setColor(Color.WHITE);
            g2d.drawString(scoreText, textX, textY);
        }
    }

    
    // --- Implementasi Metode dari GameView ---
    @Override
    public void updateDisplay() { repaint(); }

    @Override
    public void setGameElements(Player player, List<Ball> balls, Lasso lasso, Basket basket) {
        this.playerToDraw = player;
        this.ballsToDraw = balls;
        this.lassoToDraw = lasso;
        this.basketToDraw = basket;
    }

    @Override
    public void setScoreAndCount(int score, int count) {
        this.score = score;
        this.count = count;
    }


    @Override
    public void showMainMenu() {
        // Hentikan musik jika MainMenuPanel ada
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof MainMenuPanel) {
                ((MainMenuPanel) comp).stopMusic();
            }
        }
        cardLayout.show(mainPanel, "MENU");
    }

    @Override
    public void requestGameFocus() { requestFocusInWindow(); }


    // --- Implementasi Listener yang Meneruskan Input ke Presenter ---
    @Override
    public void keyPressed(KeyEvent e) { presenter.onKeyPressed(e.getKeyCode()); }

    @Override
    public void keyReleased(KeyEvent e) { presenter.onKeyReleased(e.getKeyCode()); }

    @Override
    public void keyTyped(KeyEvent e) {}
}