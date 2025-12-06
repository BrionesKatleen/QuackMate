package backEnd.services;

import backEnd.models.*;
import database.*;

public class UserService {

    // REGISTER USER
    public static User register (String username, String password, String recoveryPin){
        try {
            // Validate inputs
            if(username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if(password == null || password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }

            if(recoveryPin == null || recoveryPin.length() != 4 || !recoveryPin.matches("\\d{4}")) {
                throw new IllegalArgumentException("Recovery PIN must be 4 digits");
            }

            // Checks if username already exists
            if(DB.usernameExists(username)) {
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
            if(username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if(password == null || password.isEmpty()) {
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
        } catch(Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }

    }

    // RESET PASSWORD USING RECOVERY PIN
    public static boolean resetPassword(String username, String recoveryPin, String newPassword) {
        try{
            // Validate inputs
            if(username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if(recoveryPin == null || recoveryPin.length() != 4 || !recoveryPin.matches("\\d{4}")) {
                throw new IllegalArgumentException("Recovery PIN must be 4 digits");
            }
            if(newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters");
            }

            // Hash the new Password
            String newPasswordHash = hashPassword(newPassword);

            //RESET PASSWORD IN DATABASE
            return DB.resetPassword(username, recoveryPin, newPasswordHash);

        } catch(Exception e) {
            System.err.println("Password reset errorL " + e.getMessage());
            return false;
        }
    }

    // UPDATE USER PROFILE (COINS, EXPERIENCE, INVENTORY)
    public static boolean updateUser(User user){
        try{
            if(user == null) {
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
        try{
            return DB.usernameExists(username);
        } catch (Exception e) {
            System.err.println("Check username error: " + e.getMessage());
            return false;
        }
    }

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
        if (password == null || password.length() < 6 ){
            return false;
        }
        // AT LEAST ONE LETTER & ONE NUMBER
        return password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }
}


