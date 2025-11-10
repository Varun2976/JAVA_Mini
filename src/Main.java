import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.initialize();
            HttpServer server = new HttpServer(8080);
            server.start();
            System.out.println("Server started on http://localhost:8080");
            System.out.println("Press Ctrl+C to stop");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}