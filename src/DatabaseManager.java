import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/quizdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "jugal6#";  

    public static void initialize() throws SQLException {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS questions (" +
                "id SERIAL PRIMARY KEY, " +
                "question_text TEXT NOT NULL, " +
                "option_a TEXT NOT NULL, " +
                "option_b TEXT NOT NULL, " +
                "option_c TEXT NOT NULL, " +
                "option_d TEXT NOT NULL, " +
                "correct_answer VARCHAR(1) NOT NULL)"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS student_scores (" +
                "id SERIAL PRIMARY KEY, " +
                "student_name VARCHAR(255) NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "total_questions INTEGER NOT NULL, " +
                "completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            System.out.println("Database initialized successfully");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}