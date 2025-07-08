package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AssetLoader {

    // Variabel statis untuk menyimpan setiap gambar agar bisa diakses dari mana saja
    public static BufferedImage backgroundSprite, playerSprite, crystalBlueSprite, cargoBaySprite, crystalRedSprite, crystalGreenSprite, crystalPurpleSprite;

    /**
     * Metode utama untuk memuat semua gambar ke dalam memori saat game dimulai.
     */
    public static void loadImages() {
        System.out.println("Memuat aset gambar...");
        try {
            backgroundSprite = loadImage("assets/images/bg.png");
            playerSprite = loadImage("assets/images/Char.png");
            cargoBaySprite = loadImage("assets/images/Blackhole.png");

            // Gambar Crystal
            crystalBlueSprite = loadImage("assets/images/BlueCrystal.png");
            crystalRedSprite = loadImage("assets/images/RedCrystal.png");
            crystalGreenSprite = loadImage("assets/images/GreenCrystal.png");
            crystalPurpleSprite = loadImage("assets/images/PurpleCrystal.png");

            System.out.println("Aset berhasil dimuat.");
        } catch (IOException e) {
            System.err.println("Gagal memuat aset gambar!");
            e.printStackTrace();
            // Keluar dari program jika aset penting gagal dimuat
            System.exit(1); 
        }
    }
    
    /**
     * Fungsi pembantu untuk membaca file gambar dari path yang diberikan.
     * @param path Lokasi file gambar.
     * @return Objek BufferedImage.
     * @throws IOException jika file tidak ditemukan.
     */
    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}