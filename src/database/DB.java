package database;

import backEnd.models.*;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:quackmate.db";
    private static Connection conn;

    //-----------------------------------------
    // DATABASE CONNECTION
    //-----------------------------------------
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            // Enable foreign key support
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.close();
        }
        return conn;
    }

    //-----------------------------------------
    // CLOSE DATABASE CONNECTION
    //-----------------------------------------
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // INITIALIZE DATABASE (DROP OLD TABLES)
    //-----------------------------------------
    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");

            // Delete old database file
            File dbFile = new File("quackmate.db");
            if (dbFile.exists()) {
                closeConnection();
                if (dbFile.delete()) {
                    System.out.println("Old database deleted successfully.");
                } else {
                    System.out.println("Failed to delete old database.");
                }
            }

            Statement stmt = getConnection().createStatement();

            // **CHANGES START HERE: Complete User table with all required fields**

            // Create User table (with all attributes from User.java)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS User (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    recovery_pin TEXT,
                    coins INTEGER DEFAULT 100,
                    experience INTEGER DEFAULT 0,
                    level INTEGER DEFAULT 1,
                    owned_hats TEXT DEFAULT '[]',
                    owned_foods TEXT DEFAULT '[]',
                    created_at TEXT NOT NULL,
                    last_login TEXT,
                    is_active BOOLEAN DEFAULT 1
                )
            """);

            // Player table (simplified for game data)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Player (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES User(id) ON DELETE CASCADE
                )
            """);

            // Duck table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Duck (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_id INTEGER NOT NULL,
                    hunger REAL DEFAULT 100.0,
                    energy REAL DEFAULT 100.0,
                    cleanliness REAL DEFAULT 100.0,
                    happiness REAL DEFAULT 100.0,
                    state TEXT DEFAULT 'idle',
                    FOREIGN KEY(player_id) REFERENCES Player(id) ON DELETE CASCADE
                )
            """);

            // **NEW: Create Hat table for inventory**
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Hat (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price INTEGER NOT NULL,
                    rarity TEXT DEFAULT 'common'
                )
            """);

            // **NEW: Create Food table for inventory**
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Food (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price INTEGER NOT NULL,
                    hunger_restore INTEGER NOT NULL,
                    energy_restore INTEGER DEFAULT 0
                )
            """);

            // Insert default items
            insertDefaultItems(stmt);

            stmt.close();
            System.out.println("Database tables created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // INSERT DEFAULT ITEMS
    //-----------------------------------------
    private static void insertDefaultItems(Statement stmt) throws SQLException {
        // Default hats
        String[] defaultHats = {
                "INSERT OR IGNORE INTO Hat (name, price, rarity) VALUES ('Baseball Cap', 50, 'common')",
                "INSERT OR IGNORE INTO Hat (name, price, rarity) VALUES ('Top Hat', 200, 'rare')",
                "INSERT OR IGNORE INTO Hat (name, price, rarity) VALUES ('Party Hat', 100, 'uncommon')",
                "INSERT OR IGNORE INTO Hat (name, price, rarity) VALUES ('Crown', 500, 'legendary')"
        };

        // Default foods
        String[] defaultFoods = {
                "INSERT OR IGNORE INTO Food (name, price, hunger_restore, energy_restore) VALUES ('Bread', 10, 20, 5)",
                "INSERT OR IGNORE INTO Food (name, price, hunger_restore, energy_restore) VALUES ('Fish', 30, 50, 20)",
                "INSERT OR IGNORE INTO Food (name, price, hunger_restore, energy_restore) VALUES ('Super Seeds', 100, 100, 50)",
                "INSERT OR IGNORE INTO Food (name, price, hunger_restore, energy_restore) VALUES ('Energy Drink', 150, 10, 80)"
        };

        for (String sql : defaultHats) {
            stmt.executeUpdate(sql);
        }
        for (String sql : defaultFoods) {
            stmt.executeUpdate(sql);
        }

        System.out.println("Default items added to database.");
    }

    //-----------------------------------------
    // USER AUTHENTICATION METHODS
    //-----------------------------------------

    /**
     * Register a new user with all required fields
     * @return user ID or -1 if failed
     */
    public static int registerUser(String username, String passwordHash, String recoveryPin) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Insert user with all fields from User.java
            ps = conn.prepareStatement(
                    "INSERT INTO User (username, password_hash, recovery_pin, coins, experience, level, " +
                            "owned_hats, owned_foods, created_at, last_login, is_active) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, recoveryPin);
            ps.setInt(4, 100); // Default coins
            ps.setInt(5, 0);   // Default experience
            ps.setInt(6, 1);   // Default level
            ps.setString(7, "[]"); // Empty owned_hats JSON array
            ps.setString(8, "[]"); // Empty owned_foods JSON array
            ps.setString(9, timestamp);
            ps.setString(10, timestamp);
            ps.setBoolean(11, true);

            ps.executeUpdate();

            // Get the generated user ID
            rs = ps.getGeneratedKeys();
            int userId = rs.next() ? rs.getInt(1) : -1;
            rs.close();
            ps.close();

            if (userId != -1) {
                // Create a Player record for this user
                int playerId = createPlayerForUser(conn, userId, username);

                if (playerId != -1) {
                    // Create a Duck for this player
                    createDuckForPlayer(conn, playerId);
                }

                conn.commit(); // Commit transaction
                System.out.println("Registered User ID: " + userId);
                printAllUsers();
                return userId;
            }

            conn.rollback();
            return -1;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Registration error: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a Player record for a new user
     */
    private static int createPlayerForUser(Connection conn, int userId, String playerName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO Player (user_id, name) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, userId);
            ps.setString(2, playerName);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int playerId = rs.next() ? rs.getInt(1) : -1;
            return playerId;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Create a default duck for a player
     */
    private static void createDuckForPlayer(Connection conn, int userId) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO Duck (player_id) VALUES (?)"
            );
            ps.setInt(1, userId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Authenticate a user by username and password hash
     * @return User object if successful, null otherwise
     */
    public static User authenticateUser(String username, String passwordHash) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT * FROM User WHERE username = ? AND password_hash = ? AND is_active = 1"
            );
            ps.setString(1, username);
            ps.setString(2, passwordHash);

            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRecoveryPin(rs.getString("recovery_pin"));
                user.setCoins(rs.getInt("coins"));
                user.setExperience(rs.getInt("experience"));
                user.setLevel(rs.getInt("level"));
                user.setOwnedHats(rs.getString("owned_hats"));
                user.setOwnedFoods(rs.getString("owned_foods"));

                // Parse timestamps
                String createdAtStr = rs.getString("created_at");
                String lastLoginStr = rs.getString("last_login");

                if (createdAtStr != null) {
                    user.setCreatedAt(LocalDateTime.parse(createdAtStr));
                }
                if (lastLoginStr != null) {
                    user.setLastLogin(LocalDateTime.parse(lastLoginStr));
                }

                user.setActive(rs.getBoolean("is_active"));

                return user;
            }

            return null;

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if username already exists
     * @return true if username exists, false otherwise
     */
    public static boolean usernameExists(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT id FROM User WHERE username = ?"
            );
            ps.setString(1, username);

            rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Check username error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset password using recovery PIN
     * @return true if successful, false otherwise
     */
    public static boolean resetPassword(String username, String recoveryPin, String newPasswordHash) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "UPDATE User SET password_hash = ? WHERE username = ? AND recovery_pin = ?"
            );
            ps.setString(1, newPasswordHash);
            ps.setString(2, username);
            ps.setString(3, recoveryPin);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Password reset error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update user data (coins, experience, level, inventory, last login)
     * @return true if successful, false otherwise
     */
    public static boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            // Update last login timestamp
            String lastLoginStr = user.getLastLogin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            ps = conn.prepareStatement(
                    "UPDATE User SET coins = ?, experience = ?, level = ?, " +
                            "owned_hats = ?, owned_foods = ?, last_login = ? WHERE id = ?"
            );

            ps.setInt(1, user.getCoins());
            ps.setInt(2, user.getExperience());
            ps.setInt(3, user.getLevel());
            ps.setString(4, user.getOwnedHats());
            ps.setString(5, user.getOwnedFoods());
            ps.setString(6, lastLoginStr);
            ps.setInt(7, user.getId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update user error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get user by ID
     * @return User object or null if not found
     */
    public static User getUserById(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT * FROM User WHERE id = ? AND is_active = 1"
            );
            ps.setInt(1, userId);

            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRecoveryPin(rs.getString("recovery_pin"));
                user.setCoins(rs.getInt("coins"));
                user.setExperience(rs.getInt("experience"));
                user.setLevel(rs.getInt("level"));
                user.setOwnedHats(rs.getString("owned_hats"));
                user.setOwnedFoods(rs.getString("owned_foods"));

                String createdAtStr = rs.getString("created_at");
                String lastLoginStr = rs.getString("last_login");

                if (createdAtStr != null) {
                    user.setCreatedAt(LocalDateTime.parse(createdAtStr));
                }
                if (lastLoginStr != null) {
                    user.setLastLogin(LocalDateTime.parse(lastLoginStr));
                }

                user.setActive(rs.getBoolean("is_active"));
                return user;
            }

            return null;

        } catch (Exception e) {
            System.err.println("Get user by ID error: " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update user's coins
     * @return true if successful
     */
    public static boolean updateUserCoins(int userId, int coins) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "UPDATE User SET coins = ? WHERE id = ?"
            );
            ps.setInt(1, coins);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update coins error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update user's experience and level
     * @return true if successful
     */
    public static boolean updateUserExperience(int userId, int experience, int level) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "UPDATE User SET experience = ?, level = ? WHERE id = ?"
            );
            ps.setInt(1, experience);
            ps.setInt(2, level);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update experience error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update user's inventory
     * @return true if successful
     */
    public static boolean updateUserInventory(int userId, String ownedHats, String ownedFoods) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "UPDATE User SET owned_hats = ?, owned_foods = ? WHERE id = ?"
            );
            ps.setString(1, ownedHats);
            ps.setString(2, ownedFoods);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update inventory error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deactivate user account
     * @return true if successful
     */
    public static boolean deactivateUser(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "UPDATE User SET is_active = 0 WHERE id = ?"
            );
            ps.setInt(1, userId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Deactivate user error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //-----------------------------------------
    // PLAYER AND DUCK METHODS
    //-----------------------------------------

    /**
     * Get player ID by user ID
     */
    public static int getPlayerIdByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT id FROM Player WHERE user_id = ?"
            );
            ps.setInt(1, userId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Get player ID error: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get duck by player ID
     */
    public static ResultSet getDuckByPlayerId(int playerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT * FROM Duck WHERE player_id = ?"
            );
            ps.setInt(1, playerId);

            rs = ps.executeQuery();
            // Note: Caller is responsible for closing ResultSet
            return rs;

        } catch (SQLException e) {
            System.err.println("Get duck error: " + e.getMessage());
            return null;
        }
    }

    //-----------------------------------------
    // INVENTORY METHODS
    //-----------------------------------------

    /**
     * Get all hats from shop
     */
    public static ResultSet getAllHats() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Hat ORDER BY price");
            return rs;

        } catch (SQLException e) {
            System.err.println("Get hats error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all foods from shop
     */
    public static ResultSet getAllFoods() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Food ORDER BY price");
            return rs;

        } catch (SQLException e) {
            System.err.println("Get foods error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get hat by ID
     */
    public static ResultSet getHatById(int hatId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT * FROM Hat WHERE id = ?"
            );
            ps.setInt(1, hatId);

            rs = ps.executeQuery();
            return rs;

        } catch (SQLException e) {
            System.err.println("Get hat by ID error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get food by ID
     */
    public static ResultSet getFoodById(int foodId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(
                    "SELECT * FROM Food WHERE id = ?"
            );
            ps.setInt(1, foodId);

            rs = ps.executeQuery();
            return rs;

        } catch (SQLException e) {
            System.err.println("Get food by ID error: " + e.getMessage());
            return null;
        }
    }

    //-----------------------------------------
    // TESTING AND UTILITY METHODS
    //-----------------------------------------

    /**
     * Add sample data for testing
     */
    public static void addSampleData() {
        try {
            // Add test users if they don't exist
            if (!usernameExists("testuser")) {
                String testHash = Integer.toHexString("password123".hashCode());
                registerUser("testuser", testHash, "1234");
                System.out.println("Test user created: testuser / password123 / PIN: 1234");
            }

            if (!usernameExists("ducklover")) {
                String hash = Integer.toHexString("duck123".hashCode());
                registerUser("ducklover", hash, "4321");
                System.out.println("Test user created: ducklover / duck123 / PIN: 4321");
            }

            if (!usernameExists("quackmaster")) {
                String hash = Integer.toHexString("quack456".hashCode());
                registerUser("quackmaster", hash, "5678");
                System.out.println("Test user created: quackmaster / quack456 / PIN: 5678");
            }

        } catch (Exception e) {
            System.err.println("Error adding sample data: " + e.getMessage());
        }
    }

    /**
     * Clear all users (for testing)
     */
    public static void clearUsers() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            // Delete in correct order due to foreign keys
            stmt.execute("DELETE FROM Duck");
            stmt.execute("DELETE FROM Player");
            stmt.execute("DELETE FROM User");

            conn.commit();
            System.out.println("All users cleared from database.");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error clearing users: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Print all users (for debugging)
     */
    public static void printAllUsers() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, username, coins, level FROM User WHERE is_active = 1");

            System.out.println("\n=== Registered Users ===");
            System.out.println("ID | Username | Coins | Level");
            System.out.println("-----------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                int coins = rs.getInt("coins");
                int level = rs.getInt("level");

                System.out.printf("%2d | %-10s | %5d | %3d%n", id, username, coins, level);
            }

        } catch (SQLException e) {
            System.err.println("Error printing users: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("Database connection test: SUCCESS");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED - " + e.getMessage());
            return false;
        }
    }
}

