package backEnd.models;

public class GameResults {
    private int happinessGain;
    private int coinReward;
    private int expGain;
    private int gameScore;
    private boolean success;
    private String message;

    // Constructors
    public GameResults() {
        this.success = true;
        this.message = "Success";
    }

    public GameResults(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // GETTERS
    public int getHappinessGain() { return happinessGain; }
    public int getCoinReward() { return coinReward; }
    public int getExpGain() { return expGain; }
    public int getGameScore() { return gameScore; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    // SETTERS
    public void setHappinessGain(int happinessGain) { this.happinessGain = happinessGain; }
    public void setCoinReward(int coinReward) { this.coinReward = coinReward; }
    public void setExpGain(int expGain) { this.expGain = expGain; }
    public void setGameScore(int gameScore) { this.gameScore = gameScore; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}
