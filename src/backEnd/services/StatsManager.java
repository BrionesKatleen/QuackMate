package backEnd.services;

import backEnd.models.*;
import java.time.LocalDateTime;
import java.time.Duration;

public class StatsManager {
    // Single constant for all stat decay rates (per minute)
    private static final int STATS_DECAY_RATE = 3;

    /**
     * Update duck stats based on time passed
     */
    public void updateDuckStats(Duck duck) {
        if (!duck.isAlive()) return;

        LocalDateTime now = LocalDateTime.now();
        long minutesPassed = calculateMinutesPassed(duck.getLastFed(), now);

        applyStatDecay(duck, (int)minutesPassed);
        updateHealth(duck);
        checkSickness(duck);
    }

    /**
     * Feed duck with food
     */
    public boolean feedDuck(Duck duck, Food food) {
        if (!duck.canPerformAction()) return false;

        // Calculate dynamic effects
        int hungerReduction = calculateHungerReduction(food);
        int happinessBonus = calculateHappinessBonus(food);
        int healthRestore = food.getHealthRestore();

        // Apply effects
        duck.setHunger(Math.max(0, duck.getHunger() - hungerReduction));
        duck.setHappiness(Math.min(100, duck.getHappiness() + happinessBonus));
        duck.setHealth(Math.min(100, duck.getHealth() + healthRestore));
        duck.setState("EATING");
        duck.setLastFed(LocalDateTime.now());

        return true;
    }

    /**
     * Clean the duck
     */
    public boolean cleanDuck(Duck duck) {
        if (!duck.isAlive()) return false;

        int cleanlinessGain = 30 + (100 - duck.getCleanliness()) / 5;
        duck.setCleanliness(Math.min(100, duck.getCleanliness() + cleanlinessGain));
        duck.setState("BATHING");
        duck.setLastCleaned(LocalDateTime.now());

        return true;
    }

    /**
     * Start playing with duck
     */
    public boolean playWithDuck(Duck duck) {
        if (!duck.canPerformAction()) return false;

        duck.setState("PLAYING");
        duck.setLastPlayed(LocalDateTime.now());
        return true;
    }

    /**
     * Apply mini-game results to duck
     */
    public GameResults applyGameResults(Duck duck, int gameScore) {
        GameResults results = new GameResults();
        results.setGameScore(gameScore);

        // Calculate happiness from score
        int happinessGain = calculateHappinessFromScore(gameScore);
        duck.setHappiness(Math.min(100, duck.getHappiness() + happinessGain));

        // Calculate rewards
        int coinReward = calculateCoinReward(gameScore);
        int expGain = calculateExpFromScore(gameScore);

        // Return to idle
        duck.setState("IDLE");

        results.setHappinessGain(happinessGain);
        results.setCoinReward(coinReward);
        results.setExpGain(expGain);
        results.setSuccess(true);

        return results;
    }

    /**
     * Put duck to sleep
     */
    public boolean putDuckToSleep(Duck duck) {
        if (!duck.canPerformAction()) return false;

        duck.setState("SLEEPING");
        return true;
    }

    /**
     * Wake up duck
     */
    public boolean wakeUpDuck(Duck duck) {
        if ("SLEEPING".equals(duck.getState())) {
            duck.setState("IDLE");

            // Calculate sleep benefits
            long sleepMinutes = Duration.between(duck.getLastPlayed(), LocalDateTime.now()).toMinutes();
            int healthGain = (int)(sleepMinutes * 2);
            duck.setHealth(Math.min(100, duck.getHealth() + healthGain));

            return true;
        }
        return false;
    }

    /**
     * Heal sick duck
     */
    public boolean healDuck(Duck duck) {
        if (!duck.isAlive() || !duck.isSick()) return false;

        duck.setSick(false);
        duck.setHealth(Math.min(100, duck.getHealth() + 30));
        return true;
    }

    // ========== PRIVATE CALCULATION METHODS ==========

    private void applyStatDecay(Duck duck, int minutesPassed) {
        int decayAmount = STATS_DECAY_RATE * minutesPassed;

        // Hunger increases over time
        duck.setHunger(Math.min(100, duck.getHunger() + decayAmount));

        // Happiness decreases over time (unless already low)
        if (duck.getHappiness() > 20) {
            duck.setHappiness(Math.max(0, duck.getHappiness() - decayAmount));
        }

        // Cleanliness decreases over time
        duck.setCleanliness(Math.max(0, duck.getCleanliness() - decayAmount));
    }

    private void updateHealth(Duck duck) {
        if (!duck.isAlive()) return;

        int healthChange = 0;

        // Extreme hunger hurts health
        if (duck.getHunger() > 90) healthChange -= 8;
        else if (duck.getHunger() > 70) healthChange -= 4;

        // Unhappiness hurts health
        if (duck.getHappiness() < 10) healthChange -= 6;
        else if (duck.getHappiness() < 30) healthChange -= 3;

        // Dirtiness hurts health
        if (duck.getCleanliness() < 10) healthChange -= 7;
        else if (duck.getCleanliness() < 30) healthChange -= 3;

        // Sickness hurts health
        if (duck.isSick()) healthChange -= 10;

        duck.setHealth(Math.max(0, Math.min(100, duck.getHealth() + healthChange)));

        // Check death
        if (duck.getHealth() <= 0) {
            duck.setAlive(false);
            duck.setState("DEAD");
        }
    }

    private void checkSickness(Duck duck) {
        if (duck.isSick()) return;

        double sicknessChance = 0.0;

        if (duck.getCleanliness() < 10) sicknessChance += 0.5;
        else if (duck.getCleanliness() < 20) sicknessChance += 0.3;

        if (duck.getHealth() < 20) sicknessChance += 0.4;
        else if (duck.getHealth() < 40) sicknessChance += 0.2;

        if (duck.getHunger() > 90) sicknessChance += 0.3;

        if (Math.random() < sicknessChance) {
            duck.setSick(true);
        }
    }

    private int calculateHungerReduction(Food food) {
        String foodName = food.getName().toLowerCase();
        int base = food.getHungerRestore();

        // Food type modifiers
        switch (food.getFoodType()) {
            case "premium": base *= 1.5; break;
            case "treat": base *= 1.3; break;
            case "medicine": base *= 0.5; break;
        }

        // Price bonus
        if (food.getPrice() > 50) base *= 1.2;

        return Math.min(80, Math.max(10, base));
    }

    private int calculateHappinessBonus(Food food) {
        int base = food.getHappinessBonus();

        switch (food.getFoodType()) {
            case "treat": base *= 2; break;
            case "premium": base *= 1.5; break;
            case "medicine": base *= 0.5; break;
        }

        return Math.min(30, Math.max(0, base));
    }

    private int calculateHappinessFromScore(int score) {
        int base = score / 2; // 0-50

        if (score >= 90) base += 20;
        else if (score >= 80) base += 10;
        else if (score >= 70) base += 5;

        return Math.min(50, base);
    }

    private int calculateCoinReward(int score) {
        if (score >= 90) return 50;
        if (score >= 80) return 40;
        if (score >= 70) return 30;
        if (score >= 60) return 20;
        if (score >= 50) return 15;
        return 10;
    }

    private int calculateExpFromScore(int score) {
        return 10 + (score / 10);
    }

    private long calculateMinutesPassed(LocalDateTime from, LocalDateTime to) {
        if (from == null) return 1;
        long minutes = Duration.between(from, to).toMinutes();
        return Math.max(1, minutes);
    }

    public int getStatsDecayRate() {
        return STATS_DECAY_RATE;
    }
}