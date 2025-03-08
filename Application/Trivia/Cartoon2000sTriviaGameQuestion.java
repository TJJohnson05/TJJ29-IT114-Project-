

public record Cartoon2000sTriviaGameQuestion(String question, String answer) {
    public boolean isCorrectAnswer(String userAnswer) {
        return this.answer.equalsIgnoreCase(userAnswer);
    }
}
