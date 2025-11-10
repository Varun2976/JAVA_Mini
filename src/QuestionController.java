import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionController {

    public String getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions ORDER BY id")) {
            
            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer")
                ));
            }
        }
        return questionsToJson(questions);
    }

    public String getQuizQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions ORDER BY id")) {
            
            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer")
                ));
            }
        }
        return quizQuestionsToJson(questions);
    }

    public String addQuestion(String jsonBody) throws SQLException {
        Question question = parseQuestionFromJson(jsonBody);
        
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, question.getCorrectAnswer());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                question.setId(rs.getInt(1));
            }
        }
        return question.toJson();
    }

    public void deleteQuestion(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public String submitQuiz(String jsonBody) throws SQLException {
        String studentName = extractValue(jsonBody, "studentName");
        String answersJson = extractValue(jsonBody, "answers");
        
        int score = 0;
        int totalQuestions = 0;
        
        String[] answers = parseAnswersArray(answersJson);
        totalQuestions = answers.length;
        
        for (String answer : answers) {
            int questionId = Integer.parseInt(extractValue(answer, "questionId"));
            String selectedAnswer = extractValue(answer, "answer");
            
            if (isCorrectAnswer(questionId, selectedAnswer)) {
                score++;
            }
        }
        
        String sql = "INSERT INTO student_scores (student_name, score, total_questions) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            pstmt.setInt(2, score);
            pstmt.setInt(3, totalQuestions);
            pstmt.executeUpdate();
        }
        
        double percentage = (double) score / totalQuestions * 100;
        return String.format("{\"score\":%d,\"total\":%d,\"percentage\":%.2f}", score, totalQuestions, percentage);
    }

    public String getScoreboard() throws SQLException {
        List<StudentScore> scores = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student_scores ORDER BY score DESC, completed_at DESC")) {
            
            while (rs.next()) {
                scores.add(new StudentScore(
                    rs.getInt("id"),
                    rs.getString("student_name"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getTimestamp("completed_at")
                ));
            }
        }
        return scoresToJson(scores);
    }

    private boolean isCorrectAnswer(int questionId, String answer) throws SQLException {
        String sql = "SELECT correct_answer FROM questions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("correct_answer").equals(answer);
            }
        }
        return false;
    }

    private Question parseQuestionFromJson(String json) {
        Question q = new Question();
        q.setQuestionText(extractValue(json, "questionText"));
        q.setOptionA(extractValue(json, "optionA"));
        q.setOptionB(extractValue(json, "optionB"));
        q.setOptionC(extractValue(json, "optionC"));
        q.setOptionD(extractValue(json, "optionD"));
        q.setCorrectAnswer(extractValue(json, "correctAnswer"));
        return q;
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
        } else if (json.charAt(startIdx) == '[') {
            endIdx = json.indexOf("]", startIdx) + 1;
            return json.substring(startIdx, endIdx);
        } else {
            while (endIdx < json.length() && json.charAt(endIdx) != ',' && json.charAt(endIdx) != '}') {
                endIdx++;
            }
        }
        
        return json.substring(startIdx, endIdx);
    }

    private String[] parseAnswersArray(String answersJson) {
        List<String> answers = new ArrayList<>();
        int depth = 0;
        int start = -1;
        
        for (int i = 0; i < answersJson.length(); i++) {
            char c = answersJson.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    answers.add(answersJson.substring(start, i + 1));
                }
            }
        }
        
        return answers.toArray(new String[0]);
    }

    private String questionsToJson(List<Question> questions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            json.append(questions.get(i).toJson());
            if (i < questions.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String quizQuestionsToJson(List<Question> questions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            json.append(questions.get(i).toQuizJson());
            if (i < questions.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String scoresToJson(List<StudentScore> scores) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < scores.size(); i++) {
            json.append(scores.get(i).toJson());
            if (i < scores.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}