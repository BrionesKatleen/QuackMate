package auth;

import java.sql.*;

// HANDLES ALL SQLITE DB OPERATIONS
public class DatabaseManager {
    private static final String db_URL = "jdbc: sqlite:[FileName.db]";

    public DatabaseManager() {
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    level INTEGER DEFAULT 1,
                    coins INTEGER DEFAULT 0;
                    ownedItems TEXT DEFAULT ''
                );
                """;

        try (Connection conn = DriverManager.getConnection(db_URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DB] Table ready :)");
        } catch (SQLException e) {
            System.out.println("[DB ERROR] "  + e.getMessage());
        }
    }

    // ADDS USER TO DATABASE
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users(username, password, level, coins, ownedItems) VALUES(?, ?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(db_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, String.valueOf(user.getLevel()));
            stmt.setString(4, String.valueOf(user.getCoin()));
            stmt.setString(5, user.getOwnedItems());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("[DB INSERT ERROR] " + e.getMessage());
            return false;
        }
    }

    // GETS THE USER FROM DATABASE
    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try(Connection conn = DriverManager.getConnection(db_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("level"),
                    rs.getInt("coins"),
                    rs.getString("ownedItems")
            );
        } catch (SQLException e) {
            System.out.println("[DB GET ERROR] " + e.getMessage());
            return null;
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET password = ?, level = ?, coins = ?, ownedItems = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(db_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPassword());
            stmt.setInt(2, user.getLevel());
            stmt.setInt(3, user.getCoin());
            stmt.setString(4, user.getOwnedItems());
            stmt.setString(5, user.getUsername());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[DB UPDATE ERROR] " + e.getMessage());
        }
    }
}

