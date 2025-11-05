import java.sql.*;

public class AuthController {
    
    public String login(String jsonBody) throws SQLException {
        String username = extractValue(jsonBody, "username");
        String password = extractValue(jsonBody, "password");
        
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    "",
                    rs.getString("role")
                );
                return "{\"success\":true,\"user\":" + user.toJson() + "}";
            } else {
                return "{\"success\":false,\"message\":\"Invalid username or password\"}";
            }
        }
    }
    
    public String register(String jsonBody) throws SQLException {
        String username = extractValue(jsonBody, "username");
        String password = extractValue(jsonBody, "password");
        String role = extractValue(jsonBody, "role");
        
        if (!role.equals("TEACHER") && !role.equals("STUDENT")) {
            return "{\"success\":false,\"message\":\"Invalid role\"}";
        }
        
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt(1);
                User user = new User(id, username, "", role);
                return "{\"success\":true,\"user\":" + user.toJson() + "}";
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key")) {
                return "{\"success\":false,\"message\":\"Username already exists\"}";
            }
            throw e;
        }
        
        return "{\"success\":false,\"message\":\"Registration failed\"}";
    }
    
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int startIdx = json.indexOf(searchKey);
        if (startIdx == -1) return "";
        
        startIdx = json.indexOf(":", startIdx) + 1;
        while (startIdx < json.length() && (json.charAt(startIdx) == ' ' || json.charAt(startIdx) == '"')) {
            startIdx++;
        }
        
        int endIdx = startIdx;
        if (json.charAt(startIdx - 1) == '"') {
            endIdx = json.indexOf("\"", startIdx);
        } else {
            while (endIdx < json.length() && json.charAt(endIdx) != ',' && json.charAt(endIdx) != '}') {
                endIdx++;
            }
        }
        
        return json.substring(startIdx, endIdx);
    }
}