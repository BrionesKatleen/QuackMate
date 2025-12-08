package backEnd.services;

import backEnd.models.MiniGameSession;

public class MiniGameManager {
    private StatsManager statsManager;

    public MiniGameManager(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    /**
     * Record game session in database
     */
    public void recordGameSession(int userId, int duckId, String gameType, int score,
                                  int coinsEarned, int expEarned) {
        MiniGameSession session = new MiniGameSession(userId, duckId, gameType, score);
        session.setCoinsEarned(coinsEarned);
        session.setExpEarned(expEarned);

        database.GameSessionDAO.create(session);
    }

    /**
     * Calculate mini-game score based on performance
     */
    public int calculateScore(int correctAnswers, int totalQuestions, int timeTaken) {
        if (totalQuestions == 0) return 0;

        double accuracy = (double) correctAnswers / totalQuestions;
        double timeBonus = Math.max(0, 1.0 - (timeTaken / 300.0)); // 5 minute max

        int baseScore = (int)(accuracy * 100);
        int bonus = (int)(timeBonus * 20);

        return Math.min(100, baseScore + bonus);
    }

    /**
     * Get user's game history
     */
    public java.util.List<MiniGameSession> getUserGameHistory(int userId, int limit) {
        return database.GameSessionDAO.getByUserId(userId, limit);
    }

    /**
     * Get high scores for a game type
     */
    public java.util.List<MiniGameSession> getHighScores(String gameType, int limit) {
        return database.GameSessionDAO.getHighScores(gameType, limit);
    }
}