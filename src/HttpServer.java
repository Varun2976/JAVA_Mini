import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpServer {
    private com.sun.net.httpserver.HttpServer server;
    private QuestionController questionController;
    private AuthController authController;

    public HttpServer(int port) throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        questionController = new QuestionController();
        authController = new AuthController();
        setupRoutes();
    }

    private void setupRoutes() {
        server.createContext("/api/auth/login", this::handleLogin);
        server.createContext("/api/auth/register", this::handleRegister);
        server.createContext("/api/questions", this::handleQuestions);
        server.createContext("/api/quiz/questions", this::handleQuizQuestions);
        server.createContext("/api/quiz/submit", this::handleQuizSubmit);
        server.createContext("/api/scoreboard", this::handleScoreboard);
    }

    public void start() {
        server.setExecutor(null);
        server.start();
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String body = readRequestBody(exchange);
            String response = authController.login(body);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String body = readRequestBody(exchange);
            String response = authController.register(body);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleQuestions(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                String response = questionController.getAllQuestions();
                sendResponse(exchange, 200, response);
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                String response = questionController.addQuestion(body);
                sendResponse(exchange, 200, response);
            } else if (method.equals("DELETE")) {
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                questionController.deleteQuestion(id);
                sendResponse(exchange, 200, "{\"message\":\"Deleted\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleQuizQuestions(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String response = questionController.getQuizQuestions();
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleQuizSubmit(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String body = readRequestBody(exchange);
            String response = questionController.submitQuiz(body);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleScoreboard(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String response = questionController.getScoreboard();
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}