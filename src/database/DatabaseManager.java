////package database;
////
////import java.io.File;
////import java.sql.*;
////import java.util.Timer;
////import java.util.TimerTask;
////
////public class DatabaseManager {
////
////    private static final String DB_URL = "jdbc:sqlite:quackmate.db";
////    private static Connection conn;
////
////    //-----------------------------------------
////    // DATABASE CONNECTION
////    //-----------------------------------------
////    public static Connection getConnection() throws SQLException {
////        if (conn == null || conn.isClosed()) {
////            conn = DriverManager.getConnection(DB_URL);
////            Statement stmt = conn.createStatement();
////            // Enable foreign key support
////            stmt.execute("PRAGMA foreign_keys = ON");
////            stmt.close();
////        }
////        return conn;
////    }
////
////    //-----------------------------------------
////    // CLOSE DATABASE CONNECTION
////    //-----------------------------------------
////    public static void closeConnection() {
////        try {
////            if (conn != null && !conn.isClosed()) {
////                conn.close();
////                System.out.println("Database connection closed.");
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
////
////    //-----------------------------------------
////    // INITIALIZE DATABASE (DROP OLD TABLES)
////    //-----------------------------------------
////    public static void initialize() {
////        try {
////            Class.forName("org.sqlite.JDBC");
////
////            // Delete old database file
////            File dbFile = new File("quackmate.db");
////            if (dbFile.exists()) {
////                closeConnection();
////                if (dbFile.delete()) {
////                    System.out.println("Old database deleted successfully.");
////                } else {
////                    System.out.println("Failed to delete old database.");
////                }
////            }
////
////            Statement stmt = getConnection().createStatement();
////
////            // Create tables
////            stmt.executeUpdate("""
////                CREATE TABLE IF NOT EXISTS Player (
////                    id INTEGER PRIMARY KEY AUTOINCREMENT,
////                    name TEXT NOT NULL
////                )
////            """);
////
////            stmt.executeUpdate("""
////                CREATE TABLE IF NOT EXISTS Duck (
////                    id INTEGER PRIMARY KEY AUTOINCREMENT,
////                    player_id INTEGER NOT NULL,
////                    hunger REAL,
////                    energy REAL,
////                    cleanliness REAL,
////                    happiness REAL,
////                    state TEXT DEFAULT 'idle',
////                    FOREIGN KEY(player_id) REFERENCES Player(id)
////                )
////            """);
////
////            stmt.close();
////            System.out.println("Tables are ready!");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////
////    //-----------------------------------------
////    // ADD PLAYER
////    //-----------------------------------------
////    public static int addPlayer(String name) {
////        try {
////            PreparedStatement ps = getConnection().prepareStatement(
////                    "INSERT INTO Player(name) VALUES(?)",
////                    Statement.RETURN_GENERATED_KEYS
////            );
////            ps.setString(1, name);
////            ps.executeUpdate();
////
////            ResultSet rs = ps.getGeneratedKeys();
////            int id = rs.next() ? rs.getInt(1) : -1;
////            rs.close();
////            ps.close();
////            System.out.println("Added Player ID: " + id);
////            return id;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return -1;
////        }
////    }
////
////    //-----------------------------------------
////    // ADD DUCK
////    //-----------------------------------------
////    public static int addDuck(int playerId) {
////        try {
////            PreparedStatement ps = getConnection().prepareStatement(
////                    "INSERT INTO Duck(player_id, hunger, energy, cleanliness, happiness, state) VALUES(?, 100, 100, 100, 100, 'idle')",
////                    Statement.RETURN_GENERATED_KEYS
////            );
////            ps.setInt(1, playerId);
////
////            int rows = ps.executeUpdate();
////            System.out.println("Rows inserted into Duck: " + rows);
////
////            ResultSet rs = ps.getGeneratedKeys();
////            int id = rs.next() ? rs.getInt(1) : -1;
////            rs.close();
////            ps.close();
////            System.out.println("Added Duck ID: " + id);
////            return id;
////        } catch (SQLException e) {
////            System.out.println("Error adding duck: " + e.getMessage());
////            return -1;
////        }
////    }
////
////    //-----------------------------------------
////    // CHANGE DUCK STATE
////    //-----------------------------------------
////    public static void setDuckState(int duckId, String state) {
////        try {
////            PreparedStatement ps = getConnection().prepareStatement(
////                    "UPDATE Duck SET state=? WHERE id=?"
////            );
////            ps.setString(1, state);
////            ps.setInt(2, duckId);
////            ps.executeUpdate();
////            ps.close();
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
////
////    //-----------------------------------------
////    // GET DUCK
////    //-----------------------------------------
////    public static Duck getDuck(int duckId) {
////        try {
////            PreparedStatement ps = getConnection().prepareStatement(
////                    "SELECT * FROM Duck WHERE id=?"
////            );
////            ps.setInt(1, duckId);
////            ResultSet rs = ps.executeQuery();
////
////            Duck duck = null;
////            if (rs.next()) {
////                duck = new Duck(
////                        rs.getInt("id"),
////                        rs.getDouble("hunger"),
////                        rs.getDouble("energy"),
////                        rs.getDouble("cleanliness"),
////                        rs.getDouble("happiness"),
////                        rs.getString("state")
////                );
////            }
////            rs.close();
////            ps.close();
////            return duck;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return null;
////        }
////    }
////
////    //-----------------------------------------
////    // UPDATE STATS
////    //-----------------------------------------
////    public static void updateStats(int duckId, double hunger, double energy, double cleanliness, double happiness) {
////        try {
////            PreparedStatement ps = getConnection().prepareStatement(
////                    "UPDATE Duck SET hunger=?, energy=?, cleanliness=?, happiness=? WHERE id=?"
////            );
////            ps.setDouble(1, clamp(hunger));
////            ps.setDouble(2, clamp(energy));
////            ps.setDouble(3, clamp(cleanliness));
////            ps.setDouble(4, clamp(happiness));
////            ps.setInt(5, duckId);
////            ps.executeUpdate();
////            ps.close();
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
////
////    //-----------------------------------------
////    // CLAMP VALUES 0–100
////    //-----------------------------------------
////    private static double clamp(double value) {
////        return Math.max(0, Math.min(100, value));
////    }
////
////    //-----------------------------------------
////    // AUTO STAT UPDATE EVERY 5 SECONDS
////    //-----------------------------------------
////    public static void startStatTimer(int duckId) {
////        Timer timer = new Timer(true);
////
////        TimerTask task = new TimerTask() {
////            @Override
////            public void run() {
////                Duck d = getDuck(duckId);
////                if (d == null) return;
////
////                double hunger = d.hunger;
////                double energy = d.energy;
////                double cleanliness = d.cleanliness;
////                double happiness = d.happiness;
////
////                switch (d.state) {
////                    case "playing":
////                        energy += 0.4;
////                        hunger -= 0.03;
////                        cleanliness -= 0.025;
////                        happiness += 0.3;
////                        break;
////                    case "eating":
////                        energy += 0.2;
////                        hunger += 0.5;
////                        happiness += 0.2;
////                        break;
////                    case "sleeping":
////                        energy += 0.09;
////                        break;
////                    case "bathing":
////                        cleanliness += 0.6;
////                        happiness += 0.2;
////                        break;
////                    default:
////                        energy -= 0.025;
////                        hunger -= 0.02;
////                        cleanliness -= 0.02;
////                        happiness -= 0.02;
////                        break;
////                }
////
////                boolean isNight = java.time.LocalTime.now().getHour() >= 18 ||
////                        java.time.LocalTime.now().getHour() < 6;
////                if (isNight) energy -= 0.1;
////
////                updateStats(duckId, hunger, energy, cleanliness, happiness);
////
////                // PRINT STATS
////                System.out.println("=== DUCK STATS ===");
////                System.out.printf("Hunger: %.2f\n", hunger);
////                System.out.printf("Energy: %.2f\n", energy);
////                System.out.printf("Cleanliness: %.2f\n", cleanliness);
////                System.out.printf("Happiness: %.2f\n", happiness);
////                System.out.println("State: " + d.state);
////                System.out.println("=================\n");
////            }
////        };
////
////        task.run(); // first immediate print
////        timer.scheduleAtFixedRate(task, 5_000, 5_000); // every 5 seconds
////    }
////
////    //-----------------------------------------
////    // DUCK CLASS
////    //---------------------------------
////    public static class Duck {
////        public int id;
////        public double hunger, energy, cleanliness, happiness;
////        public String state;
////
////        public Duck(int id, double hunger, double energy, double cleanliness, double happiness, String state) {
////            this.id = id;
////            this.hunger = hunger;
////            this.energy = energy;
////            this.cleanliness = cleanliness;
////            this.happiness = happiness;
////            this.state = state;
////        }
////    }
////}
//package database;
//
//import java.sql.*;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class DatabaseManager {
//
//    private static final String DB_URL = "jdbc:sqlite:quackmate.db";
//    private static Connection conn;
//
//    // Constructor = start database + create tables + start timer
//    public DatabaseManager() {
//        connect();
//        createTables();
//        insertDefaultData();
//        startAutoUpdateTimer();
//    }
//
//    // ---------- 1. CONNECT TO DATABASE ----------
//    private void connect() {
//        try {
//            conn = DriverManager.getConnection(DB_URL);
//            System.out.println("Database Connected!");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ---------- 2. CREATE TABLES ----------
//    private void createTables() {
//        String playersTable = """
//                CREATE TABLE IF NOT EXISTS players (
//                    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    name TEXT
//                );
//                """;
//
//        String ducksTable = """
//                CREATE TABLE IF NOT EXISTS ducks (
//                    duck_id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    player_id INTEGER,
//                    hunger REAL,
//                    energy REAL,
//                    cleanliness REAL,
//                    happiness REAL
//                );
//                """;
//
//        try (Statement stmt = conn.createStatement()) {
//            stmt.execute(playersTable);
//            stmt.execute(ducksTable);
//            System.out.println("Tables Ready!");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ---------- 3. INSERT DEFAULT PLAYER + DUCK ----------
//    private void insertDefaultData() {
//        try {
//            // Insert player if none exists
//            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) AS count FROM players");
//            if (rs.getInt("count") == 0) {
//                conn.createStatement().execute("INSERT INTO players (name) VALUES ('DefaultPlayer')");
//                System.out.println("Default player created!");
//            }
//
//            // Insert duck if none exists
//            rs = conn.createStatement().executeQuery("SELECT COUNT(*) AS count FROM ducks");
//            if (rs.getInt("count") == 0) {
//                conn.createStatement().execute("""
//                        INSERT INTO ducks (player_id, hunger, energy, cleanliness, happiness)
//                        VALUES (1, 100, 100, 100, 100)
//                        """);
//                System.out.println("Default duck created!");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ---------- 4. LOAD DUCK DATA ----------
//    public void loadDuckStats() {
//        try {
//            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ducks WHERE duck_id = 1");
//
//            if (!rs.next()) {
//                System.out.println("ERROR: Duck not found!");
//                return;
//            }
//
//            System.out.println("=== CURRENT DUCK STATS ===");
//            System.out.println("Hunger: " + rs.getDouble("hunger"));
//            System.out.println("Energy: " + rs.getDouble("energy"));
//            System.out.println("Cleanliness: " + rs.getDouble("cleanliness"));
//            System.out.println("Happiness: " + rs.getDouble("happiness"));
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ---------- 5. AUTOMATIC STAT DECREASE (EVERY MINUTE) ----------
//    private void startAutoUpdateTimer() {
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//                updateDuckStats();
//            }
//
//        }, 0, 60000); // every 1 minute
//    }
//
//    private void updateDuckStats() {
//        try {
//            String sql = "SELECT * FROM ducks WHERE duck_id = 1";
//            ResultSet rs = conn.createStatement().executeQuery(sql);
//
//            if (!rs.next()) {
//                System.out.println("Debug: Duck not found!");
//                return;
//            }
//
//            // Current stats
//            double hunger = rs.getDouble("hunger");
//            double energy = rs.getDouble("energy");
//            double cleanliness = rs.getDouble("cleanliness");
//            double happiness = rs.getDouble("happiness");
//
//            // Apply auto-decrease rules
//            hunger -= 0.02;
//            energy -= 0.25;
//            cleanliness -= 0.02;
//            happiness -= 0.02;
//
//            // Prevent negative values
//            hunger = Math.max(hunger, 0);
//            energy = Math.max(energy, 0);
//            cleanliness = Math.max(cleanliness, 0);
//            happiness = Math.max(happiness, 0);
//
//            // Save back to DB
//            PreparedStatement ps = conn.prepareStatement("""
//                    UPDATE ducks
//                    SET hunger=?, energy=?, cleanliness=?, happiness=?
//                    WHERE duck_id=1
//                    """);
//
//            ps.setDouble(1, hunger);
//            ps.setDouble(2, energy);
//            ps.setDouble(3, cleanliness);
//            ps.setDouble(4, happiness);
//            ps.executeUpdate();
//
//            System.out.println("Stats updated automatically!");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ---------- 6. ACTION METHODS ----------
//    public void feedDuck() {
//        modifyStat("hunger", +20);
//        modifyStat("happiness", +5);
//    }
//
//    public void cleanDuck() {
//        modifyStat("cleanliness", +25);
//        modifyStat("happiness", +5);
//    }
//
//    public void playDuck() {
//        modifyStat("happiness", +10);
//        modifyStat("energy", -5);
//    }
//
//    public void putDuckToSleep() {
//        modifyStat("energy", +40);
//    }
//
//    private void modifyStat(String stat, double change) {
//        try {
//            PreparedStatement ps = conn.prepareStatement(
//                    "UPDATE ducks SET " + stat + " = " + stat + " + ? WHERE duck_id=1"
//            );
//            ps.setDouble(1, change);
//            ps.executeUpdate();
//
//            System.out.println(stat + " changed by " + change);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
package database;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:quackmate.db";
    private static Connection conn;

    //-----------------------------------------
    // DATABASE CONNECTION
    //-----------------------------------------
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
        }
        return conn;
    }

    //-----------------------------------------
    // CLOSE CONNECTION
    //-----------------------------------------
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // INITIALIZE DATABASE
    //-----------------------------------------
    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");

            // Delete old DB
            File dbFile = new File("quackmate.db");
            if (dbFile.exists()) {
                closeConnection();
                dbFile.delete();
            }

            Statement stmt = getConnection().createStatement();

            // Create tables
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Player (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL
                    )
            """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Duck (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_id INTEGER NOT NULL,
                        hunger REAL,
                        energy REAL,
                        cleanliness REAL,
                        happiness REAL,
                        state TEXT DEFAULT 'idle',
                        FOREIGN KEY(player_id) REFERENCES Player(id)
                    )
            """);

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // ADD PLAYER
    //-----------------------------------------
    public static int addPlayer(String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "INSERT INTO Player(name) VALUES(?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : -1;
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //-----------------------------------------
    // ADD DUCK
    //-----------------------------------------
    public static int addDuck(int playerId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "INSERT INTO Duck(player_id, hunger, energy, cleanliness, happiness, state) VALUES(?, 100, 100, 100, 100, 'idle')",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, playerId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : -1;
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //-----------------------------------------
    // GET EXISTING DUCK
    //-----------------------------------------
    public static int getExistingDuck(int playerId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "SELECT id FROM Duck WHERE player_id=? LIMIT 1"
            );
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            int id = rs.next() ? rs.getInt("id") : -1;
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //-----------------------------------------
    // GET DUCK OBJECT
    //-----------------------------------------
    public static Duck getDuck(int duckId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "SELECT * FROM Duck WHERE id=?"
            );
            ps.setInt(1, duckId);
            ResultSet rs = ps.executeQuery();

            Duck duck = null;
            if (rs.next()) {
                duck = new Duck(
                        rs.getInt("id"),
                        rs.getDouble("hunger"),
                        rs.getDouble("energy"),
                        rs.getDouble("cleanliness"),
                        rs.getDouble("happiness"),
                        rs.getString("state")
                );
            }
            rs.close();
            ps.close();
            return duck;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //-----------------------------------------
    // UPDATE STATS
    //-----------------------------------------
    public static void updateStats(int duckId, double hunger, double energy, double cleanliness, double happiness) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "UPDATE Duck SET hunger=?, energy=?, cleanliness=?, happiness=? WHERE id=?"
            );
            ps.setDouble(1, clamp(hunger));
            ps.setDouble(2, clamp(energy));
            ps.setDouble(3, clamp(cleanliness));
            ps.setDouble(4, clamp(happiness));
            ps.setInt(5, duckId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // CHANGE DUCK STATE (PLAYER ACTION)
    //-----------------------------------------
    public static void setDuckState(int duckId, String state) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
                    "UPDATE Duck SET state=? WHERE id=?"
            );
            ps.setString(1, state);
            ps.setInt(2, duckId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------
    // CLAMP VALUES
    //-----------------------------------------
    private static double clamp(double value) {
        return Math.max(0, Math.min(100, value));
    }

    //-----------------------------------------
    // AUTO STAT UPDATE TIMER (EVERY 5 SECONDS)
    //-----------------------------------------
    public static void startStatTimer(int duckId) {
        Timer timer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Duck d = getDuck(duckId);
                if (d == null) return;

                double hunger = d.hunger;
                double energy = d.energy;
                double cleanliness = d.cleanliness;
                double happiness = d.happiness;

                // Update based on current state
                switch (d.state) {
                    case "playing":
                        energy += 0.4;
                        hunger -= 0.03;
                        cleanliness -= 0.025;
                        happiness += 0.3;
                        break;
                    case "eating":
                        energy += 0.2;
                        hunger += 0.5;
                        happiness += 0.2;
                        break;
                    case "sleeping":
                        energy += 0.09;
                        break;
                    case "bathing":
                        cleanliness += 0.6;
                        happiness += 0.2;
                        break;
                    default: // idle
                        energy -= 0.025;
                        hunger -= 0.02;
                        cleanliness -= 0.02;
                        happiness -= 0.02;
                        break;
                }

                // Save updated stats
                updateStats(duckId, hunger, energy, cleanliness, happiness);

                // Print stats to console
                System.out.printf("Duck ID: %d | Hunger: %.2f | Energy: %.2f | Cleanliness: %.2f | Happiness: %.2f | State: %s\n",
                        duckId, clamp(hunger), clamp(energy), clamp(cleanliness), clamp(happiness), d.state);

                // Return to idle
                if (!d.state.equals("idle")) {
                    setDuckState(duckId, "idle");
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 5000); // every 5 seconds
    }

    //-----------------------------------------
    // DUCK CLASS
    //-----------------------------------------
    public static class Duck {
        public int id;
        public double hunger, energy, cleanliness, happiness;
        public String state;

        public Duck(int id, double hunger, double energy, double cleanliness, double happiness, String state) {
            this.id = id;
            this.hunger = hunger;
            this.energy = energy;
            this.cleanliness = cleanliness;
            this.happiness = happiness;
            this.state = state;
        }
    }
}


