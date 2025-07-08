import view.GamePanel;
import view.MainMenuPanel;
import util.AssetLoader; // TAMBAHKAN IMPORT INI
import view.GamePanel;
import javax.swing.*;
import java.awt.*;

public class CollectTheSkillBallsGame {

    public static void main(String[] args) {
        AssetLoader.loadImages();
        // Menjalankan pembuatan GUI di Event Dispatch Thread (EDT) untuk keamanan thread Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Collect The Skill Balls - MVP & Thread");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Menengahkan window di layar
            frame.setResizable(false);

            // CardLayout digunakan untuk beralih antara tampilan yang berbeda (menu dan game)
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Inisialisasi semua panel View
            GamePanel gamePanel = new GamePanel(mainPanel, cardLayout);
            
            // INI ADALAH BARIS YANG DIPERBAIKI:
            // Konstruktor MainMenuPanel dipanggil dengan 3 argumen yang benar.
            MainMenuPanel mainMenuPanel = new MainMenuPanel(mainPanel, cardLayout, gamePanel.getPresenter());

            // Menambahkan panel-panel ke dalam container CardLayout dengan nama unik
            mainPanel.add(mainMenuPanel, "MENU");
            mainPanel.add(gamePanel, "GAME");

            // Menambahkan container utama ke frame dan menampilkannya
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}