package model;

import java.awt.Rectangle;

/**
 * Kelas Basket merepresentasikan titik kumpul (keranjang) tempat pemain
 * harus mendepositkan bola yang telah ditangkap.
 */
public class Basket {
    // Variabel untuk menyimpan posisi PUSAT dan ukuran keranjang
    private final int x, y, width, height;

    /**
     * Konstruktor untuk Basket.
     * @param x Posisi x TENGAH dari keranjang.
     * @param y Posisi y TENGAH dari keranjang.
     * @param width Lebar gambar aset keranjang.
     * @param height Tinggi gambar aset keranjang.
     */
    public Basket(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Menghasilkan kotak deteksi tabrakan (hitbox) yang akurat.
     * Dihitung dari titik pusat (x, y) agar sesuai dengan gambar yang ditampilkan.
     * @return Objek Rectangle untuk deteksi tabrakan.
     */
    public Rectangle getBounds() {
        // Hitung posisi sudut kiri atas dari hitbox
        int hitboxX = x - width / 2 + 17;
        int hitboxY = y - height / 2;
        return new Rectangle(hitboxX, hitboxY, width, height);
    }

    // Getter untuk mendapatkan posisi pusat
    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    // Catatan: Metode draw() yang lama sudah tidak diperlukan lagi
    // karena GamePanel sekarang langsung menggambar aset gambarnya.
}