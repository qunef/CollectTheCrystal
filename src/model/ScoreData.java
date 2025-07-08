package model;

// Representasi tabel 'thasil'
public class ScoreData {
    private String username;
    private int score;
    private int count;

    public ScoreData(String username, int score, int count) {
        this.username = username;
        this.score = score;
        this.count = count;
    }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getCount() { return count; }
}