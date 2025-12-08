package database;

import java.sql.*;

public class DB {
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:duck_game.db";

    // Singleton instance
    private static DB instance;

    private DB() {
        // Private constructor for singleton
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    /**
     * Establish database connection
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to database");
            initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Close database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database");
            }
        } catch (SQLException e) {
            System.err.println("Disconnect failed: " + e.getMessage());
        }
    }

    /**
     * Execute INSERT/UPDATE/DELETE queries
     */
    public boolean executeUpdate(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute SELECT queries
     */
    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Prepare statement with parameters
     */
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            System.err.println("Prepare statement failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Initialize all database tables
     */
    public void initializeDatabase() {
        createUserTable();
        createDuckTable();
        createFoodTable();
        createHatTable();
        createGameSessionTable();
        insertDefaultData();
    }

    private void createUserTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                recovery_pin TEXT,
                coins INTEGER DEFAULT 100,
                experience INTEGER DEFAULT 0,
                level INTEGER DEFAULT 1,
                owned_hats TEXT DEFAULT '',
                owned_foods TEXT DEFAULT '',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT 1
            )
        """;
        executeUpdate(sql);
        System.out.println("Users table ready");
    }

    private void createDuckTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ducks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                health INTEGER DEFAULT 100,
                hunger INTEGER DEFAULT 0,
                happiness INTEGER DEFAULT 100,
                cleanliness INTEGER DEFAULT 100,
                state TEXT DEFAULT 'IDLE',
                is_alive BOOLEAN DEFAULT 1,
                is_sick BOOLEAN DEFAULT 0,
                equipped_hat TEXT,
                last_fed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_cleaned TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """;
        executeUpdate(sql);
        System.out.println("Ducks table ready");
    }

    private void createFoodTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS foods (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price INTEGER NOT NULL,
                hunger_restore INTEGER DEFAULT 20,
                health_restore INTEGER DEFAULT 0,
                happiness_bonus INTEGER DEFAULT 5,
                image_path TEXT,
                food_type TEXT DEFAULT 'basic'
            )
        """;
        executeUpdate(sql);
        System.out.println("Foods table ready");
    }

    private void createHatTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS hats (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price INTEGER NOT NULL,
                happiness_bonus INTEGER DEFAULT 5,
                image_path TEXT
            )
        """;
        executeUpdate(sql);
        System.out.println("Hats table ready");
    }

    private void createGameSessionTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS game_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                duck_id INTEGER NOT NULL,
                game_type TEXT NOT NULL,
                score INTEGER,
                coins_earned INTEGER DEFAULT 0,
                exp_earned INTEGER DEFAULT 0,
                played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (duck_id) REFERENCES ducks(id) ON DELETE CASCADE
            )
        """;
        executeUpdate(sql);
        System.out.println("Game sessions table ready");
    }

    private void insertDefaultData() {
        // Insert default foods if table is empty
        String checkFoods = "SELECT COUNT(*) as count FROM foods";
        ResultSet rs = executeQuery(checkFoods);
        try {
            if (rs != null && rs.next() && rs.getInt("count") == 0) {
                String[] foods = {
                        "INSERT INTO foods (name, description, price, hunger_restore, happiness_bonus, food_type) VALUES ('Bread', 'Basic duck food', 5, 15, 3, 'basic')",
                        "INSERT INTO foods (name, description, price, hunger_restore, happiness_bonus, food_type) VALUES ('Worms', 'Duck favorite!', 10, 25, 8, 'treat')",
                        "INSERT INTO foods (name, description, price, hunger_restore, happiness_bonus, health_restore, food_type) VALUES ('Premium Meal', 'Nutritious meal', 25, 40, 12, 10, 'premium')",
                        "INSERT INTO foods (name, description, price, hunger_restore, happiness_bonus, health_restore, food_type) VALUES ('Medicine', 'Makes duck feel better', 15, 5, -5, 30, 'medicine')"
                };

                for (String sql : foods) {
                    executeUpdate(sql);
                }
                System.out.println("Default foods inserted");
            }
        } catch (SQLException e) {
            System.err.println("Error checking food table: " + e.getMessage());
        }

        // Insert default hats if table is empty
        String checkHats = "SELECT COUNT(*) as count FROM hats";
        rs = executeQuery(checkHats);
        try {
            if (rs != null && rs.next() && rs.getInt("count") == 0) {
                String[] hats = {
                        "INSERT INTO hats (name, description, price, happiness_bonus) VALUES ('Baseball Cap', 'Sporty look', 20, 5)",
                        "INSERT INTO hats (name, description, price, happiness_bonus) VALUES ('Top Hat', 'Fancy formal hat', 50, 10)",
                        "INSERT INTO hats (name, description, price, happiness_bonus) VALUES ('Party Hat', 'Celebration time!', 30, 8)",
                        "INSERT INTO hats (name, description, price, happiness_bonus) VALUES ('Crown', 'For royalty', 100, 15)"
                };

                for (String sql : hats) {
                    executeUpdate(sql);
                }
                System.out.println("Default hats inserted");
            }
        } catch (SQLException e) {
            System.err.println("Error checking hat table: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}