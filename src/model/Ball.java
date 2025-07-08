package model;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Kelas Ball merepresentasikan objek kristal energi yang dikumpulkan oleh pemain.
 * Posisi (x,y) dianggap sebagai PUSAT dari bola/kristal.
 */
public class Ball {
    public enum BallType {
        RED(10),     // Merah = 10 poin
        BLUE(25),    // Biru = 25 poin
        GREEN(50),   // Hijau = 50 poin
        PURPLE(100); // Ungu = 100 poin

        public final int scoreValue;

        BallType(int scoreValue) {
            this.scoreValue = scoreValue;
        }
    }

    private int x, y, speedX, score;
    private boolean caught = false;
    private BallType type;
    private final int width = 38; 
    private final int height = 60;

    public Ball(int x, int y, int speedX, BallType type) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.type = type;
        this.score = type.scoreValue; // Skor berdasarkan jenis bola
    }

    public BallType getType() {
        return this.type;
    }


    /**
     * Menggerakkan bola secara horizontal jika belum tertangkap.
     */
    public void move() {
        if (!caught) {
            x += speedX;
        }
    }
    
    /**
     * Membuat bola mengikuti sebuah titik target.
     * Digunakan saat bola ditarik oleh tali lasso.
     * @param targetX Posisi x target.
     * @param targetY Posisi y target.
     */
    public void follow(int targetX, int targetY) {
        double angle = Math.atan2(targetY - y, targetX - x);
        x += (int)(4 * Math.cos(angle));
        y += (int)(4 * Math.sin(angle));
    }
    
    /**
     * Metode gambar fallback (tidak digunakan lagi setelah beralih ke aset gambar).
     * Berguna untuk debugging.
     * @param g2d Objek Graphics2D untuk menggambar.
     */
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillOval(x - width/2, y - height/2, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(score), x + 1, y - 2);
    }

    /**
     * Menghasilkan kotak deteksi tabrakan (hitbox) yang akurat dan berpusat.
     * @return Objek Rectangle untuk deteksi tabrakan.
     */

    // --- Getter dan Setter ---
    public Rectangle getBounds() {
        // Hitung posisi sudut kiri atas dari hitbox agar (x,y) menjadi pusatnya
        int hitboxX = x - width / 2;
        int hitboxY = y - height / 2;
        return new Rectangle(hitboxX, hitboxY, width, height);
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getScore() { 
        return score; 
    }
    
    public boolean isCaught() { 
        return caught; 
    }
    
    public void setCaught(boolean caught) { 
        this.caught = caught; 
    }
}