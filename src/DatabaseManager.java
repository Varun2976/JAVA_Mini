import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/quizdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "jugal6#";  // ⚠️ CHANGE THIS!

    public static void initialize() throws SQLException {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Users table for authentication
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL CHECK (role IN ('TEACHER', 'STUDENT')))"
            );
            
            // Questions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS questions (" +
                "id SERIAL PRIMARY KEY, " +
                "question_text TEXT NOT NULL, " +
                "option_a TEXT NOT NULL, " +
                "option_b TEXT NOT NULL, " +
                "option_c TEXT NOT NULL, " +
                "option_d TEXT NOT NULL, " +
                "correct_answer VARCHAR(1) NOT NULL, " +
                "teacher_id INTEGER REFERENCES users(id))"
            );

            // Student scores table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS student_scores (" +
                "id SERIAL PRIMARY KEY, " +
                "student_id INTEGER REFERENCES users(id), " +
                "score INTEGER NOT NULL, " +
                "total_questions INTEGER NOT NULL, " +
                "completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            // Create default teacher and student if they don't exist
            String checkUser = "SELECT COUNT(*) FROM users WHERE username = ?";
            
            // Default teacher: username=teacher, password=teacher123
            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, "teacher");
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertTeacher = "INSERT INTO users (username, password, role) VALUES (?, ?, 'TEACHER')";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertTeacher)) {
                        insertPs.setString(1, "teacher");
                        insertPs.setString(2, "teacher123");
                        insertPs.executeUpdate();
                        System.out.println("Default teacher created - username: teacher, password: teacher123");
                    }
                }
            }
            
            // Default student: username=student, password=student123
            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, "student");
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertStudent = "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertStudent)) {
                        insertPs.setString(1, "student");
                        insertPs.setString(2, "student123");
                        insertPs.executeUpdate();
                        System.out.println("Default student created - username: student, password: student123");
                    }
                }
            }

            System.out.println("Database initialized successfully");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}