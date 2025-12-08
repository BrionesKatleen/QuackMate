package backEnd.services;

import backEnd.models.*;
import java.util.List;

public class DuckService {

    /**
     * Create new duck for user
     */
    public Duck createDuck(int userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Duck name cannot be empty");
        }

        // Check if user exists
        UserService userService = new UserService();
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }

        Duck duck = new Duck(userId, name);
        return database.DuckDAO.create(duck);
    }

    /**
     * Get duck by ID
     */
    public Duck getDuckById(int duckId) {
        return database.DuckDAO.getById(duckId);
    }

    /**
     * Get all ducks for user
     */
    public List<Duck> getDucksByUser(int userId) {
        return database.DuckDAO.getByUserId(userId);
    }

    /**
     * Update duck in database
     */
    public boolean updateDuck(Duck duck) {
        if (duck == null) return false;
        return database.DuckDAO.update(duck);
    }

    /**
     * Delete duck
     */
    public boolean deleteDuck(int duckId) {
        return database.DuckDAO.delete(duckId);
    }

    /**
     * Feed duck (combines duck stat update with inventory management)
     */
    public boolean feedDuck(UserService userService, User user, Duck duck, int foodId) {
        if (!duck.canPerformAction()) {
            System.out.println("Duck cannot eat right now");
            return false;
        }

        // Get food details from database
        backEnd.models.Food food = database.FoodDAO.getById(foodId);
        if (food == null) {
            System.out.println("Food not found");
            return false;
        }

        // Check if user owns this food
        if (!userService.ownsFood(user, foodId)) {
            System.out.println("User doesn't own this food");
            return false;
        }

        // Use the food from inventory
        if (!userService.useFood(user, foodId)) {
            System.out.println("Failed to use food from inventory");
            return false;
        }

        // Feed the duck using StatsManager
        StatsManager statsManager = new StatsManager();
        boolean success = statsManager.feedDuck(duck, food);

        if (success) {
            // Give user experience for feeding
            userService.addExperience(user, 5);

            // Save duck changes
            updateDuck(duck);

            System.out.println("Fed " + duck.getName() + " with " + food.getName());
        }

        return success;
    }

    /**
     * Clean duck
     */
    public boolean cleanDuck(Duck duck) {
        if (!duck.isAlive()) return false;

        StatsManager statsManager = new StatsManager();
        boolean success = statsManager.cleanDuck(duck);

        if (success) {
            updateDuck(duck);
        }

        return success;
    }

    /**
     * Start playing with duck
     */
    public boolean startPlayingWithDuck(Duck duck) {
        StatsManager statsManager = new StatsManager();
        return statsManager.playWithDuck(duck);
    }

    /**
     * Put duck to sleep
     */
    public boolean putDuckToSleep(Duck duck) {
        StatsManager statsManager = new StatsManager();
        return statsManager.putDuckToSleep(duck);
    }

    /**
     * Wake up duck
     */
    public boolean wakeUpDuck(Duck duck) {
        StatsManager statsManager = new StatsManager();
        return statsManager.wakeUpDuck(duck);
    }

    /**
     * Heal sick duck
     */
    public boolean healDuck(Duck duck) {
        StatsManager statsManager = new StatsManager();
        boolean success = statsManager.healDuck(duck);

        if (success) {
            updateDuck(duck);
        }

        return success;
    }

    /**
     * Check duck status and apply time-based updates
     */
    public void updateDuckStats(Duck duck) {
        StatsManager statsManager = new StatsManager();
        statsManager.updateDuckStats(duck);
        updateDuck(duck);
    }

    /**
     * Get duck status message
     */
    public String getDuckStatus(Duck duck) {
        return duck.getStatusMessage();
    }

    /**
     * Count user's ducks
     */
    public int countUserDucks(int userId) {
        return getDucksByUser(userId).size();
    }
}
