package model;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Kelas Lasso merepresentasikan tali yang digunakan oleh pemain untuk menangkap bola.
 * Tali ini akan memanjang ke arah klik mouse, menangkap bola yang disentuh,
 * dan menariknya kembali ke arah pemain.
 */
public class Lasso {
    private Player player;
    private int startX, startY, endX, endY, targetX, targetY;
    private enum State { INACTIVE, EXTENDING, RETRACTING }
    private State state;
    private Ball caughtBall;
    private final int speed = 15;

    /**
     * Konstruktor untuk Lasso.
     * @param player Objek pemain yang menembakkan lasso.
     */
    public Lasso(Player player) {
        this.player = player;
        this.state = State.INACTIVE;
    }

    /**
     * Mengaktifkan lasso untuk ditembakkan ke arah target.
     * @param targetX Koordinat x target klik mouse.
     * @param targetY Koordinat y target klik mouse.
     */
    public void activate(int targetX, int targetY) {
        // Hanya bisa aktif jika tidak sedang memegang bola
        if (player.getHeldBall() != null) return;
        
        this.startX = player.getX() + 10; // Posisi tengah pemain
        this.startY = player.getY() + 10;
        this.endX = startX;
        this.endY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.state = State.EXTENDING;
    }

    /**
     * Memperbarui posisi dan state lasso (memanjang atau menarik kembali).
     */
    public void update() {
        if (state == State.EXTENDING) {
            // Logika tali memanjang ke arah target
            double angle = Math.atan2(targetY - startY, targetX - startX);
            endX += (int) (speed * Math.cos(angle));
            endY += (int) (speed * Math.sin(angle));

            // Jika sudah mencapai target, otomatis menarik kembali
            if (Math.hypot(endX - startX, endY - startY) >= Math.hypot(targetX - startX, targetY - startY)) {
                state = State.RETRACTING;
            }
        } else if (state == State.RETRACTING) {
            // Logika tali menarik kembali ke arah PEMAIN
            int targetX = player.getX() + 10;
            int targetY = player.getY() + 10;
            double angle = Math.atan2(targetY - endY, targetX - endX);
            endX += (int) (speed * Math.cos(angle));
            endY += (int) (speed * Math.sin(angle));

            // Bola yang tertangkap akan ikut ditarik
            if (caughtBall != null) {
                caughtBall.follow(endX, endY);
            }

            // Cek jika sudah sampai di dekat pemain
            if (Math.hypot(endX - targetX, endY - targetY) < speed) {
                if (caughtBall == null) {
                    state = State.INACTIVE; // Reset jika tidak membawa bola
                }
                // Jika membawa bola, Presenter akan meresetnya
            }
        }
    }
    
    /**
     * Menggambar tali lasso di layar.
     * @param g2d Objek Graphics2D untuk menggambar.
     */
    public void draw(Graphics2D g2d) {
        if (state != State.INACTIVE) {
            g2d.setColor(Color.YELLOW);
            g2d.drawLine(player.getX() + 10, player.getY() + 10, endX, endY);
        }
    }
    
    /**
     * Menandai bola sebagai bola yang tertangkap oleh lasso.
     * @param ball Bola yang ditangkap.
     */
    public void catchBall(Ball ball) {
        this.caughtBall = ball;
        this.caughtBall.setCaught(true);
        this.state = State.RETRACTING;
    }
    
    /**
     * Mengecek apakah tali lasso bersinggungan dengan sebuah bola.
     * @param ball Bola yang akan dicek.
     * @return true jika bersinggungan, false jika tidak.
     */
    public boolean intersects(Ball ball) {
        return ball.getBounds().intersectsLine(player.getX() + 10, player.getY() + 10, endX, endY);
    }
    
    /**
     * Mengecek apakah tali lasso sudah kembali ke pemain sambil membawa bola.
     * @return true jika sudah kembali, false jika belum.
     */
    public boolean hasReturnedToPlayer() {
        int targetX = player.getX() + 10;
        int targetY = player.getY() + 10;
        return state == State.RETRACTING && caughtBall != null &&
               Math.hypot(endX - targetX, endY - targetY) < speed + 5;
    }

    /**
     * Mereset state lasso menjadi tidak aktif.
     */
    public void resetAfterRetraction() {
        this.state = State.INACTIVE;
        this.caughtBall = null;
    }

    // Getter untuk state dan bola yang ditangkap
    public boolean isActive() { return state != State.INACTIVE; }
    public boolean isExtending() { return state == State.EXTENDING; }
    public Ball getRetractedBall() { return caughtBall; }
}