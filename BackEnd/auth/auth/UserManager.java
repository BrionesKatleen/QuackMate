package auth;

import exceptions.AuthException;

public class UserManager {
    private final DatabaseManager db; // Attribute to store the Database

    public UserManager(){
        db = new DatabaseManager(); // connect to SQLite DB
    }

    // REGISTER NEW USER
    public void register(String username, String password) throws AuthException {
        //Check Username already exists
        if(db.getUser(username) != null) {
            throw new AuthException("Username already exists! :("); // Message if true
        }

        // Create New User w/ default lvl = 1, coins = 0, ownedItems = ""
        User newUser = new User(username, password, null, null, null);

        // Save user to DB
        boolean success = db.insertUser(newUser);
        if(!success) {
            throw new AuthException("Failed to create user in the database! :(");
        }

        System.out.println("[REGISTER] User created: " + username);
    }

    // LOGIN  USER
    public User login(String username, String password) throws AuthException {
        User user = db.getUser(username); // access the db and gets the user w/ the username

        // Username & Password Logic
        if(user == null) {
            throw new AuthException("User does not exists! :(");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthException("Invalid password! :(");
        }

        System.out.println("[LOGIN] User logged in " + username);

        return user;
    }

    // ADDS COINS
    public void addCoins(User user, int amount) {
        user.addCoins(amount); // update the object
        db.updateUser(user);  // update the database
        System.out.println("[COINS] Added " + amount + " coins to " + user.getUsername());
    }

    public void levelUp(User user) {
        user.levelUp();
        db.updateUser(user);
        System.out.println("[LEVEL] User " + user.getUsername() + " leveled up to " + user.getLevel());
    }

    // ADD OWNED ITEMS
    public void addItem(User user, String item) {
        user.addItems(item); // Add item to object
        db.updateUser(user); // Update the database
        System.out.println("[ITEM] added " + item + " to " + user.getUsername());
    }
}