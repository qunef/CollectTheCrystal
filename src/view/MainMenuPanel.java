package view;

import model.ScoreData;
import presenter.GamePresenter;
import model.DatabaseConnection;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Tampilan awal sesuai dengan layout dari gambar yang diberikan
public class MainMenuPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final GamePresenter gamePresenter;
    private Clip musicClip;
    private JTextField usernameField; // TAMBAHKAN: Field untuk input username

    public MainMenuPanel(JPanel mainPanel, CardLayout cardLayout, GamePresenter gamePresenter) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.gamePresenter = gamePresenter;

        // UBAH: Menggunakan GridBagLayout
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Judul "COLLECT THE SKILL BALLS"
        JLabel titleLabel = new JLabel("COLLECT THE SKILL BALLS");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Rentangkan judul melewati 3 kolom
        gbc.insets = new Insets(0, 0, 20, 0); // Beri jarak bawah
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // 2. Label "Username"
        JLabel usernameLabel = new JLabel("Username");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.insets = new Insets(0, 0, 10, 5);
        gbc.anchor = GridBagConstraints.EAST; // Rata kanan
        add(usernameLabel, gbc);

        // 3. Text Field untuk Username
        usernameField = new JTextField(15); // Lebar 15 karakter
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Izinkan field ini untuk meregang secara horizontal
        gbc.fill = GridBagConstraints.HORIZONTAL; // Isi ruang horizontal yang tersedia
        gbc.anchor = GridBagConstraints.WEST; // Rata kiri
        add(usernameField, gbc);

        // 4. Tombol "Play"
        JButton playButton = new JButton("Play");
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0; // Jangan meregang
        gbc.fill = GridBagConstraints.NONE; // Ukuran tetap
        gbc.insets = new Insets(0, 5, 10, 0);
        add(playButton, gbc);

        // 5. Tabel Skor
        JTable scoreTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.setColumnIdentifiers(new String[]{"Username", "Score", "Count"});
        scoreTable.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setMaximumSize(new Dimension(400, 200)); // Batasi tinggi maksimum agar scroll bar muncul
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scoreTable.setRowHeight(28); // Tinggi baris agar lebih banyak baris muat
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 0; // Jangan biarkan tabel membesar vertikal
        gbc.fill = GridBagConstraints.BOTH; // Isi semua ruang yang tersedia
        gbc.insets = new Insets(0, 0, 10, 0);
        add(scrollPane, gbc);

        // 6. Tombol "Quit"
        JButton quitButton = new JButton("Quit");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0; // Jangan meregang
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(quitButton, gbc);
        
        // Mengisi tabel dengan data dari database
        refreshScoreTable(tableModel);

        // Action Listeners untuk tombol
        playButton.addActionListener(e -> {
            String username = usernameField.getText();
            if (username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (musicClip != null) musicClip.stop();
            playMusic(); // Musik hanya diputar saat game dimulai
            gamePresenter.startGame(username); // Berikan username ke presenter
            cardLayout.show(mainPanel, "GAME");
            mainPanel.getComponents()[1].requestFocusInWindow();
        });

        quitButton.addActionListener(e -> System.exit(0));
    }

    // Metode untuk mengambil data skor dan memperbarui tabel
    private void refreshScoreTable(DefaultTableModel tableModel) {
        // Hapus data lama
        tableModel.setRowCount(0);
        // Ambil data baru dari DB
        List<ScoreData> scores = getScoresFromDatabase();
        for (ScoreData score : scores) {
            tableModel.addRow(new Object[]{score.getUsername(), score.getScore(), score.getCount()});
        }
    }

    private List<ScoreData> getScoresFromDatabase() {
        // (Metode ini tidak diubah, tetap sama seperti sebelumnya)
        List<ScoreData> list = new ArrayList<>();
        String sql = "SELECT username, skor, count FROM thasil ORDER BY skor DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ScoreData(rs.getString("username"), rs.getInt("skor"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat skor dari database:\n" + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return list;
    }

    public void playMusic() {
        // (Metode ini tidak diubah, tetap sama seperti sebelumnya)
        try {
            File musicFile = new File("assets/musics/bs.wav");
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error memutar musik: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }
}