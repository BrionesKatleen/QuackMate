package backEnd.models;

import java.time.LocalDateTime;

public class MiniGameSession {
    private int id;
    private int userId;
    private int duckId;
    private String gameType;      // "catch", "memory", "racing"
    private int score;            // 0-100
    private int coinsEarned;
    private int expEarned;
    private LocalDateTime playedAt;

    // Constructors
    public MiniGameSession() {
        this.playedAt = LocalDateTime.now();
    }

    public MiniGameSession(int userId, int duckId, String gameType, int score) {
        this();
        this.userId = userId;
        this.duckId = duckId;
        this.gameType = gameType;
        this.score = score;
    }

    // GETTERS
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getDuckId() { return duckId; }
    public String getGameType() { return gameType; }
    public int getScore() { return score; }
    public int getCoinsEarned() { return coinsEarned; }
    public int getExpEarned() { return expEarned; }
    public LocalDateTime getPlayedAt() { return playedAt; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDuckId(int duckId) { this.duckId = duckId; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    public void setScore(int score) {
        if (score < 0) score = 0;
        if (score > 100) score = 100;
        this.score = score;
    }
    public void setCoinsEarned(int coinsEarned) { this.coinsEarned = coinsEarned; }
    public void setExpEarned(int expEarned) { this.expEarned = expEarned; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    // LOGIC METHODS
    public String getGameResult() {
        if (score >= 90) return "Perfect! " + score + "/100";
        if (score >= 80) return "Great! " + score + "/100";
        if (score >= 70) return "Good! " + score + "/100";
        if (score >= 60) return "OK! " + score + "/100";
        return "Practice more! " + score + "/100";
    }
}
