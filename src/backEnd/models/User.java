package backEnd.models;

import java.time.LocalDateTime;
public class User {
    private int Id; // auto-incremented in the database
    private String username;
    private String passwordHash;
    private String recoveryPin;
    private int coins;
    private int experience;
    private int level;
    private String ownedHats;
    private String ownedFoods;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;

    // Constructor
    public User(){ }

    public User(String username, String passwordHash){
        this.username = username;
        this.passwordHash = passwordHash;
        this.recoveryPin = "";
        this.coins = 100;
        this.experience = 0;
        this.level = 1;
        this.ownedHats = "[]";
        this.ownedFoods = "[]";
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
        this.isActive = true;
    }

    // GETTERS
    public int getId() { return Id;}
    public String getUsername() { return username;}
    public String getPasswordHash() { return passwordHash;}
    public String getRecoveryPin() { return recoveryPin;}
    public int getCoins() { return coins;}
    public int getExperience() {return experience;}
    public int getLevel() {return level;}
    public String getOwnedHats() {return ownedHats;}
    public String getOwnedFoods() {return ownedFoods;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getLastLogin() {return lastLogin;}
    public boolean isActive() {return isActive;}



    // SETTERS
    public void setId(int Id) {this.Id = Id;}
    public void setUsername(String username) {this.username = username;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
    public void setRecoveryPin(String recoveryPin) {this.recoveryPin = recoveryPin;}
    public void setCoins(int coins) {this.coins = coins;}
    public void setExperience(int experience) {this.experience = experience;}
    public void setLevel(int level) {this.level = level;}
    public void setOwnedHats(String ownedHats) {this.ownedHats = ownedHats;}
    public void setOwnedFoods(String ownedFoods) {this.ownedFoods = ownedFoods;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setLastLogin(LocalDateTime lastLogin) {this.lastLogin = lastLogin;}
    public void setActive(boolean active) {isActive = active;}

    // LOGIC
     public void addExperience(int exp) {
        this.experience += exp;
        this.level = (this.experience / 100) + 1; // Every 100 exp = 1 level
     }

     public void addCoins(int amount) {this.coins += amount; }

    public boolean spendCoins(int amount) { // reduce the coins if spent
        if(this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

}
