package backEnd.services;

import backEnd.models.*;
import database.*;

public class UserService {

    // ========== AUTHENTICATION METHODS (EXISTING) ==========

    // REGISTER USER
    public static User register(String username, String password, String recoveryPin) {
        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }

            if (recoveryPin == null || recoveryPin.length() != 4 || !recoveryPin.matches("\\d{4}")) {
                throw new IllegalArgumentException("Recovery PIN must be 4 digits");
            }

            // Checks if username already exists
            if (DB.usernameExists(username)) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Hash the password
            String passwordHash = hashPassword(password);

            // Register user in database
            int userId = DB.registerUser(username, passwordHash, recoveryPin);

            if (userId != -1) {
                return new User(username, passwordHash); // RETURN NEWLY CREATED USER
            }
            return null;
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return null;
        }
    }

    // LOGIN USER
    public static User login(String username, String password) {
        try {
            // VALIDATE INPUTS
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            // Hash the password for comparison
            String passwordHash = Integer.toHexString(password.hashCode());

            // AUTHENTICATE USER
            User user = DB.authenticateUser(username, passwordHash);

            if (user != null) {
                user.updateLastLogin();
                DB.updateUser(user); // Update last login in DATABASE
                return user;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    // RESET PASSWORD USING RECOVERY PIN
    public static boolean resetPassword(String username, String recoveryPin, String newPassword) {
        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (recoveryPin == null || recoveryPin.length() != 4 || !recoveryPin.matches("\\d{4}")) {
                throw new IllegalArgumentException("Recovery PIN must be 4 digits");
            }
            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters");
            }

            // Hash the new Password
            String newPasswordHash = hashPassword(newPassword);

            // RESET PASSWORD IN DATABASE
            return DB.resetPassword(username, recoveryPin, newPasswordHash);

        } catch (Exception e) {
            System.err.println("Password reset error: " + e.getMessage());
            return false;
        }
    }

    // UPDATE USER PROFILE (COINS, EXPERIENCE, INVENTORY)
    public static boolean updateUser(User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }

            return DB.updateUser(user);
        } catch (Exception e) {
            System.err.println("Update user error: " + e.getMessage());
            return false;
        }
    }

    // CHECKS IF USERNAME EXISTS IN DATABASE
    public static boolean checkUsernameExists(String username) {
        try {
            return DB.usernameExists(username);
        } catch (Exception e) {
            System.err.println("Check username error: " + e.getMessage());
            return false;
        }
    }

    // ========== INVENTORY MANAGEMENT METHODS (NEW) ==========

    /**
     * Check if user owns a specific hat
     */
    public static boolean ownsHat(User user, int hatId) {
        if (user == null) return false;

        String ownedHats = user.getOwnedHats();
        if (ownedHats == null || ownedHats.isEmpty() || ownedHats.equals("[]")) return false;

        // Remove brackets and split by commas
        String cleanHats = ownedHats.replace("[", "").replace("]", "");
        if (cleanHats.isEmpty()) return false;

        String[] hats = cleanHats.split(",");
        for (String hat : hats) {
            try {
                if (Integer.parseInt(hat.trim()) == hatId) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        return false;
    }

    /**
     * Check if user owns a specific food
     */
    public static boolean ownsFood(User user, int foodId) {
        if (user == null) return false;

        String ownedFoods = user.getOwnedFoods();
        if (ownedFoods == null || ownedFoods.isEmpty() || ownedFoods.equals("[]")) return false;

        // Remove brackets and split by commas
        String cleanFoods = ownedFoods.replace("[", "").replace("]", "");
        if (cleanFoods.isEmpty()) return false;

        String[] foods = cleanFoods.split(",");
        for (String food : foods) {
            try {
                if (Integer.parseInt(food.trim()) == foodId) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        return false;
    }

    /**
     * Add hat to user's inventory
     */
    public static boolean addHat(User user, int hatId) {
        if (user == null) return false;

        // Check if already owns the hat
        if (ownsHat(user, hatId)) {
            return true; // Already owns it
        }

        String currentHats = user.getOwnedHats();
        // Remove brackets if present
        currentHats = currentHats.replace("[", "").replace("]", "").trim();

        String newHats;
        if (currentHats.isEmpty()) {
            newHats = "[" + hatId + "]";
        } else {
            newHats = "[" + currentHats + "," + hatId + "]";
        }

        user.setOwnedHats(newHats);
        return updateUser(user);
    }

    /**
     * Add food to user's inventory
     */
    public static boolean addFood(User user, int foodId, int quantity) {
        if (user == null || quantity <= 0) return false;

        String currentFoods = user.getOwnedFoods();
        // Remove brackets if present
        currentFoods = currentFoods.replace("[", "").replace("]", "").trim();

        // For multiple quantities, we add the same ID multiple times
        StringBuilder newFoods = new StringBuilder("[");
        if (!currentFoods.isEmpty()) {
            newFoods.append(currentFoods);
        }

        for (int i = 0; i < quantity; i++) {
            if (newFoods.length() > 1 && !newFoods.toString().endsWith("[")) {
                newFoods.append(",");
            }
            newFoods.append(foodId);
        }
        newFoods.append("]");

        user.setOwnedFoods(newFoods.toString());
        return updateUser(user);
    }

    /**
     * Use food from inventory (remove one instance)
     */
    public static boolean useFood(User user, int foodId) {
        if (user == null || !ownsFood(user, foodId)) return false;

        String currentFoods = user.getOwnedFoods();
        // Remove brackets
        currentFoods = currentFoods.replace("[", "").replace("]", "").trim();

        if (currentFoods.isEmpty()) return false;

        String[] foods = currentFoods.split(",");
        StringBuilder newFoods = new StringBuilder("[");
        boolean removed = false;

        for (String food : foods) {
            int id = Integer.parseInt(food.trim());
            if (id != foodId || removed) {
                if (newFoods.length() > 1) newFoods.append(",");
                newFoods.append(id);
            } else {
                removed = true; // Remove first occurrence
            }
        }
        newFoods.append("]");

        user.setOwnedFoods(newFoods.toString());
        return updateUser(user);
    }

    /**
     * Remove hat from inventory
     */
    public static boolean removeHat(User user, int hatId) {
        if (user == null || !ownsHat(user, hatId)) return false;

        String currentHats = user.getOwnedHats();
        // Remove brackets
        currentHats = currentHats.replace("[", "").replace("]", "").trim();

        if (currentHats.isEmpty()) return false;

        String[] hats = currentHats.split(",");
        StringBuilder newHats = new StringBuilder("[");
        boolean first = true;

        for (String hat : hats) {
            int id = Integer.parseInt(hat.trim());
            if (id != hatId) {
                if (!first) newHats.append(",");
                newHats.append(id);
                first = false;
            }
        }
        newHats.append("]");

        user.setOwnedHats(newHats.toString());
        return updateUser(user);
    }

    // ========== STATS MANAGEMENT METHODS (NEW) ==========

    /**
     * Add experience to user with auto-level calculation
     */
    public static boolean addExperience(User user, int exp) {
        if (user == null || exp <= 0) return false;

        int oldLevel = user.getLevel();
        user.addExperience(exp);

        // Check for level up
        if (user.getLevel() > oldLevel) {
            // Level up bonus
            int levelUps = user.getLevel() - oldLevel;
            int bonusCoins = levelUps * 50;
            user.addCoins(bonusCoins);
            System.out.println("Level up! " + user.getUsername() + " reached level " + user.getLevel());
        }

        return updateUser(user);
    }

    /**
     * Add coins to user
     */
    public static boolean addCoins(User user, int amount) {
        if (user == null || amount <= 0) return false;

        user.addCoins(amount);
        return updateUser(user);
    }

    /**
     * Spend coins if user has enough
     */
    public static boolean spendCoins(User user, int amount) {
        if (user == null || amount <= 0) return false;

        if (user.spendCoins(amount)) {
            return updateUser(user);
        }
        return false;
    }

    /**
     * Check if user can afford an item
     */
    public static boolean canAfford(User user, int price) {
        return user != null && user.getCoins() >= price;
    }

    /**
     * Get list of owned hat IDs
     */
    public static int[] getOwnedHatIds(User user) {
        if (user == null) return new int[0];

        String ownedHats = user.getOwnedHats();
        if (ownedHats == null || ownedHats.isEmpty() || ownedHats.equals("[]")) {
            return new int[0];
        }

        String cleanHats = ownedHats.replace("[", "").replace("]", "").trim();
        if (cleanHats.isEmpty()) return new int[0];

        String[] hatStrings = cleanHats.split(",");
        int[] hatIds = new int[hatStrings.length];

        for (int i = 0; i < hatStrings.length; i++) {
            try {
                hatIds[i] = Integer.parseInt(hatStrings[i].trim());
            } catch (NumberFormatException e) {
                hatIds[i] = -1;
            }
        }

        return hatIds;
    }

    /**
     * Get list of owned food IDs
     */
    public static int[] getOwnedFoodIds(User user) {
        if (user == null) return new int[0];

        String ownedFoods = user.getOwnedFoods();
        if (ownedFoods == null || ownedFoods.isEmpty() || ownedFoods.equals("[]")) {
            return new int[0];
        }

        String cleanFoods = ownedFoods.replace("[", "").replace("]", "").trim();
        if (cleanFoods.isEmpty()) return new int[0];

        String[] foodStrings = cleanFoods.split(",");
        int[] foodIds = new int[foodStrings.length];

        for (int i = 0; i < foodStrings.length; i++) {
            try {
                foodIds[i] = Integer.parseInt(foodStrings[i].trim());
            } catch (NumberFormatException e) {
                foodIds[i] = -1;
            }
        }

        return foodIds;
    }

    /**
     * Count how many of a specific food user owns
     */
    public static int countFood(User user, int foodId) {
        if (user == null) return 0;

        String ownedFoods = user.getOwnedFoods();
        if (ownedFoods == null || ownedFoods.isEmpty() || ownedFoods.equals("[]")) {
            return 0;
        }

        String cleanFoods = ownedFoods.replace("[", "").replace("]", "").trim();
        if (cleanFoods.isEmpty()) return 0;

        String[] foods = cleanFoods.split(",");
        int count = 0;

        for (String food : foods) {
            try {
                if (Integer.parseInt(food.trim()) == foodId) {
                    count++;
                }
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }

        return count;
    }

    // ========== UTILITY METHODS (EXISTING) ==========

    private static String hashPassword(String password) {
        int hash = password.hashCode(); // Simple hash
        return Integer.toHexString(hash);
    }

    public static boolean validateUsername(String username) {
        if (username == null || username.length() <= 3 || username.length() > 28) {
            return false;
        }

        // ALLOW ALPHANUMERIC & UNDERSCORES
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // AT LEAST ONE LETTER & ONE NUMBER
        return password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }

    // ========== NEW UTILITY METHODS ==========

    /**
     * Get user by ID
     */
    public static User getUserById(int userId) {
        try {
            return DB.getUserById(userId);
        } catch (Exception e) {
            System.err.println("Get user by ID error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update user coins
     */
    public static boolean updateCoins(User user, int coins) {
        if (user == null) return false;
        user.setCoins(coins);
        return updateUser(user);
    }

    /**
     * Update user experience and level
     */
    public static boolean updateExperience(User user, int experience, int level) {
        if (user == null) return false;
        user.setExperience(experience);
        user.setLevel(level);
        return updateUser(user);
    }

    /**
     * Update user inventory (hats and foods)
     */
    public static boolean updateInventory(User user, String ownedHats, String ownedFoods) {
        if (user == null) return false;
        user.setOwnedHats(ownedHats);
        user.setOwnedFoods(ownedFoods);
        return updateUser(user);
    }

    /**
     * Deactivate user account
     */
    public static boolean deactivateUser(int userId) {
        try {
            return DB.deactivateUser(userId);
        } catch (Exception e) {
            System.err.println("Deactivate user error: " + e.getMessage());
            return false;
        }
    }
}
