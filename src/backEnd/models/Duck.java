package backEnd.models;

import java.time.LocalDateTime;

public class Duck {
    private int id;
    private int userId;
    private String name;
    private int health;       // 0-100
    private int hunger;       // 0-100
    private int happiness;    // 0-100
    private int cleanliness;  // 0-100
    private String state;     // "IDLE", "EATING", "SLEEPING", "PLAYING", "BATHING", "SICK", "DEAD"
    private boolean isAlive;
    private boolean isSick;
    private String equippedHat; // null or hat ID as string
    private LocalDateTime lastFed;
    private LocalDateTime lastCleaned;
    private LocalDateTime lastPlayed;
    private LocalDateTime createdAt;

    // Constructors
    public Duck() {
        this.health = 100;
        this.hunger = 0;
        this.happiness = 100;
        this.cleanliness = 100;
        this.state = "IDLE";
        this.isAlive = true;
        this.isSick = false;
        this.createdAt = LocalDateTime.now();
        this.lastFed = LocalDateTime.now();
        this.lastCleaned = LocalDateTime.now();
        this.lastPlayed = LocalDateTime.now();
    }

    public Duck(int userId, String name) {
        this();
        this.userId = userId;
        this.name = name;
    }

    // GETTERS
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getHunger() { return hunger; }
    public int getHappiness() { return happiness; }
    public int getCleanliness() { return cleanliness; }
    public String getState() { return state; }
    public boolean isAlive() { return isAlive; }
    public boolean isSick() { return isSick; }
    public String getEquippedHat() { return equippedHat; }
    public LocalDateTime getLastFed() { return lastFed; }
    public LocalDateTime getLastCleaned() { return lastCleaned; }
    public LocalDateTime getLastPlayed() { return lastPlayed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setHealth(int health) {
        if (health < 0) health = 0;
        if (health > 100) health = 100;
        this.health = health;
    }
    public void setHunger(int hunger) {
        if (hunger < 0) hunger = 0;
        if (hunger > 100) hunger = 100;
        this.hunger = hunger;
    }
    public void setHappiness(int happiness) {
        if (happiness < 0) happiness = 0;
        if (happiness > 100) happiness = 100;
        this.happiness = happiness;
    }
    public void setCleanliness(int cleanliness) {
        if (cleanliness < 0) cleanliness = 0;
        if (cleanliness > 100) cleanliness = 100;
        this.cleanliness = cleanliness;
    }
    public void setState(String state) { this.state = state; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public void setSick(boolean sick) { isSick = sick; }
    public void setEquippedHat(String equippedHat) { this.equippedHat = equippedHat; }
    public void setLastFed(LocalDateTime lastFed) { this.lastFed = lastFed; }
    public void setLastCleaned(LocalDateTime lastCleaned) { this.lastCleaned = lastCleaned; }
    public void setLastPlayed(LocalDateTime lastPlayed) { this.lastPlayed = lastPlayed; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // LOGIC METHODS
    public boolean canPerformAction() {
        return isAlive && !isSick && state.equals("IDLE");
    }

    public String getStatusMessage() {
        if (!isAlive) return "💀 RIP";
        if (isSick) return "🤒 Sick";
        if (hunger > 80) return "🍽️ Very Hungry";
        if (happiness < 20) return "😞 Very Sad";
        if (cleanliness < 20) return "💩 Very Dirty";
        if (health < 30) return "🏥 Needs Care";
        if (happiness > 80) return "😄 Very Happy";
        return "😊 Doing Well";
    }

    public void resetStats() {
        this.health = 100;
        this.hunger = 0;
        this.happiness = 100;
        this.cleanliness = 100;
        this.state = "IDLE";
        this.isAlive = true;
        this.isSick = false;
        this.lastFed = LocalDateTime.now();
        this.lastCleaned = LocalDateTime.now();
        this.lastPlayed = LocalDateTime.now();
    }
}
