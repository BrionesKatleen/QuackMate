package test;

import auth.User;
import auth.UserManager;
import auth.DatabaseManager;
import exceptions.AuthException;

public class AuthTest {

    public static void main(String[] args) {

        System.out.println("==== AUTH TEST START ====");

        // Create UserManager (this auto creates DB table)
        UserManager userManager = new UserManager();
        DatabaseManager db = new DatabaseManager(); // for direct tests if needed

        // 1. Test registration
        try {
            System.out.println("\n--- REGISTER TEST ---");
            userManager.register("kez", "1234");
            System.out.println("Registration OK!");

        } catch (AuthException e) {
            System.out.println("Register FAILED: " + e.getMessage());
        }

        // 2. Test login
        try {
            System.out.println("\n--- LOGIN TEST ---");
            User loggedUser = userManager.login("kez", "1234");
            System.out.println("Login OK! Welcome " + loggedUser.getUsername());

        } catch (AuthException e) {
            System.out.println("Login FAILED: " + e.getMessage());
        }

        // 3. Load user directly from DB
        try {
            System.out.println("\n--- GET USER FROM DATABASE TEST ---");
            User user = db.getUser("kez");

            if (user != null) {
                System.out.println("User found in DB:");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Level: " + user.getLevel());
                System.out.println("Coins: " + user.getCoin());
                System.out.println("Owned Items: " + user.getOwnedItems());
            } else {
                System.out.println("User not found in DB!");
            }

        } catch (Exception e) {
            System.out.println("DB GET FAILED: " + e.getMessage());
        }

        // 4. Add Coins Test
        try {
            System.out.println("\n--- ADD COINS TEST ---");
            User u = db.getUser("kez");
            userManager.addCoins(u, 50);
            System.out.println("Coins updated!");

        } catch (Exception e) {
            System.out.println("Add coins FAILED: " + e.getMessage());
        }

        // 5. Level Up Test
        try {
            System.out.println("\n--- LEVEL UP TEST ---");
            User u = db.getUser("kez");
            userManager.levelUp(u);
            System.out.println("User level increased!");

        } catch (Exception e) {
            System.out.println("Level Up FAILED: " + e.getMessage());
        }

        // 6. Add Item Test
        try {
            System.out.println("\n--- ADD ITEM TEST ---");
            User u = db.getUser("kez");
            userManager.addItem(u, "Sword");
            userManager.addItem(u, "Shield");
            System.out.println("Item added!");

        } catch (Exception e) {
            System.out.println("Add Item FAILED: " + e.getMessage());
        }

        // 7. Display Final User Data
        System.out.println("\n--- FINAL USER DATA ---");
        User finalUser = db.getUser("kez");
        System.out.println("Username: " + finalUser.getUsername());
        System.out.println("Password: " + finalUser.getPassword());
        System.out.println("Level: " + finalUser.getLevel());
        System.out.println("Coins: " + finalUser.getCoin());
        System.out.println("Items: " + finalUser.getOwnedItems());

        System.out.println("\n==== AUTH TEST END ====");
    }
}
