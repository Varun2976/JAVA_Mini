public class Question {
    private int id;
    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctAnswer;

    public Question() {}

    public Question(int id, String questionText, String optionA, String optionB, 
                   String optionC, String optionD, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"questionText\":\"%s\",\"optionA\":\"%s\",\"optionB\":\"%s\"," +
            "\"optionC\":\"%s\",\"optionD\":\"%s\",\"correctAnswer\":\"%s\"}",
            id, escapeJson(questionText), escapeJson(optionA), escapeJson(optionB),
            escapeJson(optionC), escapeJson(optionD), correctAnswer
        );
    }

    public String toQuizJson() {
        return String.format(
            "{\"id\":%d,\"questionText\":\"%s\",\"optionA\":\"%s\",\"optionB\":\"%s\"," +
            "\"optionC\":\"%s\",\"optionD\":\"%s\"}",
            id, escapeJson(questionText), escapeJson(optionA), escapeJson(optionB),
            escapeJson(optionC), escapeJson(optionD)
        );
    }

    private String escapeJson(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}