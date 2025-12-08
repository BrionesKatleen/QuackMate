package backEnd.services;

import backEnd.models.*;
import java.util.List;
import java.util.ArrayList;

public class GameManager {
    private User currentUser;
    private List<Duck> currentDucks;
    private sessionManager sessionManager;
    private UserService userService;
    private DuckService duckService;
    private StatsManager statsManager;
    private MiniGameManager miniGameManager;

    public GameManager() {
        this.userService = new UserService();
        this.duckService = new DuckService();
        this.statsManager = new StatsManager();
        this.miniGameManager = new MiniGameManager(statsManager);
        this.sessionManager = new sessionManager();
        this.currentDucks = new ArrayList<>();
    }

    /**
     * Login user
     */
    public boolean login(String username, String passwordHash) {
        User user = userService.login(username, passwordHash);
        if (user != null) {
            currentUser = user;
            currentDucks = duckService.getDucksByUser(user.getId());
            return true;
        }
        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        saveGameState();
        currentUser = null;
        currentDucks.clear();
    }

    /**
     * Register new user
     */
    public User register(String username, String passwordHash, String recoveryPin) {
        User user = userService.register(username, passwordHash, recoveryPin);
        if (user != null) {
            // Create first duck for new user
            duckService.createDuck(user.getId(), "Quacky");
        }
        return user;
    }

    /**
     * Get current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get user's ducks
     */
    public List<Duck> getUserDucks() {
        return currentDucks;
    }

    /**
     * Get duck by ID
     */
    public Duck getDuckById(int duckId) {
        for (Duck duck : currentDucks) {
            if (duck.getId() == duckId) {
                return duck;
            }
        }
        return null;
    }

    /**
     * Create new duck
     */
    public boolean createNewDuck(String name) {
        if (currentUser == null) return false;

        Duck newDuck = duckService.createDuck(currentUser.getId(), name);
        if (newDuck != null) {
            currentDucks.add(newDuck);
            return true;
        }
        return false;
    }

    /**
     * Feed duck
     */
    public boolean feedDuck(int duckId, int foodId) {
        if (currentUser == null) return false;

        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duckService.feedDuck(userService, currentUser, duck, foodId);
    }

    /**
     * Clean duck
     */
    public boolean cleanDuck(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duckService.cleanDuck(duck);
    }

    /**
     * Play mini-game with duck
     */
    public GameResults playMiniGame(int duckId, String gameType, int gameScore) {
        if (currentUser == null) return new GameResults(false, "Not logged in");

        Duck duck = getDuckById(duckId);
        if (duck == null) return new GameResults(false, "Duck not found");

        // Start playing
        if (!duckService.startPlayingWithDuck(duck)) {
            return new GameResults(false, "Duck cannot play right now");
        }

        // Apply game results
        GameResults results = statsManager.applyGameResults(duck, gameScore);

        if (results.isSuccess()) {
            // Reward user
            userService.addCoins(currentUser, results.getCoinReward());
            userService.addExperience(currentUser, results.getExpGain());

            // Save duck
            duckService.updateDuck(duck);

            // Save user
            userService.updateUser(currentUser);

            // Record game session
            miniGameManager.recordGameSession(currentUser.getId(), duckId, gameType, gameScore,
                    results.getCoinReward(), results.getExpGain());
        }

        return results;
    }

    /**
     * Put duck to sleep
     */
    public boolean putDuckToSleep(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duckService.putDuckToSleep(duck);
    }

    /**
     * Wake up duck
     */
    public boolean wakeUpDuck(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duckService.wakeUpDuck(duck);
    }

    /**
     * Heal duck
     */
    public boolean healDuck(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duckService.healDuck(duck);
    }

    /**
     * Buy item from shop
     */
    public boolean buyItem(String itemType, int itemId) {
        if (currentUser == null) return false;

        if ("hat".equalsIgnoreCase(itemType)) {
            return buyHat(itemId);
        } else if ("food".equalsIgnoreCase(itemType)) {
            return buyFood(itemId);
        }
        return false;
    }

    private boolean buyHat(int hatId) {
        // Get hat details
        Hat hat = database.HatDAO.getById(hatId);
        if (hat == null) return false;

        // Check if user can afford
        if (!userService.spendCoins(currentUser, hat.getPrice())) {
            return false;
        }

        // Add to inventory
        if (userService.addHat(currentUser, hatId)) {
            System.out.println("Purchased hat: " + hat.getName());
            return true;
        }

        return false;
    }

    private boolean buyFood(int foodId) {
        // Get food details
        Food food = database.FoodDAO.getById(foodId);
        if (food == null) return false;

        // Check if user can afford
        if (!userService.spendCoins(currentUser, food.getPrice())) {
            return false;
        }

        // Add to inventory
        if (userService.addFood(currentUser, foodId, 1)) {
            System.out.println("Purchased food: " + food.getName());
            return true;
        }

        return false;
    }

    /**
     * Update all ducks' stats (called periodically)
     */
    public void updateGameState() {
        if (currentUser == null) return;

        for (Duck duck : currentDucks) {
            duckService.updateDuckStats(duck);
        }
    }

    /**
     * Save current game state
     */
    public void saveGameState() {
        if (currentUser != null) {
            // Save user
            userService.updateUser(currentUser);

            // Save all ducks
            for (Duck duck : currentDucks) {
                duckService.updateDuck(duck);
            }
        }
    }

    /**
     * Check if duck is available for actions
     */
    public boolean isDuckAvailable(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return false;

        return duck.canPerformAction();
    }

    /**
     * Get duck status message
     */
    public String getDuckStatus(int duckId) {
        Duck duck = getDuckById(duckId);
        if (duck == null) return "Duck not found";

        return duckService.getDuckStatus(duck);
    }
}