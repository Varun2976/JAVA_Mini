import java.sql.Timestamp;

public class StudentScore {
    private int id;
    private int studentId;
    private String studentName;
    private int score;
    private int totalQuestions;
    private Timestamp completedAt;

    public StudentScore() {}

    public StudentScore(int id, int studentId, String studentName, int score, 
                       int totalQuestions, Timestamp completedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"studentName\":\"%s\",\"score\":%d,\"totalQuestions\":%d," +
            "\"completedAt\":\"%s\"}",
            id, studentName, score, totalQuestions, completedAt.toString()
        );
    }
}