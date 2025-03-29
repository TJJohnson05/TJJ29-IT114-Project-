
// Tyler Johnson 
// March 7th 2025
// Tjj29@njit.edu 
// IT114 - 004
// Phase 2 Assignment: Trivia Game Flow



public record Cartoon2000sTriviaGameQuestion(String question, String answer) {
    public boolean isCorrectAnswer(String userAnswer) {
        return this.answer.equalsIgnoreCase(userAnswer);
    }
}
