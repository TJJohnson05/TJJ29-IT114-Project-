
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




public class Cartoon2000sTriviaGameServer extends Hub {

    private final static int PORT = 37829;

    private Cartoon2000sTriviaGameState state;
    private Cartoon2000sTriviaGameQuestionList questions;
    private int currentQuestionIndex = -1;
    private Map<Integer, String> answersReceived;

    public Cartoon2000sTriviaGameServer() throws IOException {
        super(PORT);
        setAutoreset(true);
        state = new Cartoon2000sTriviaGameState();
        initializeNewGame();
    }

    private void initializeNewGame() {
        state.clearScores();
        sendToAll(state);
        questions = new Cartoon2000sTriviaGameQuestionList();
        currentQuestionIndex = -1;
        answersReceived = new HashMap<>();
    }

    @Override
    protected void messageReceived(int playerID, Object message) {
        if (message instanceof String) {
            String command = ((String) message).trim();

            if (command.equalsIgnoreCase("restart")) {
                if (state.getPlayerCount() >= 2) {
                    sendToAll("A new game is starting!");
                    initializeNewGame();
                    startGame();
                } else {
                    sendToAll("Waiting for at least 2 players to start a new game.");
                }
            } else {
                handleAnswer(playerID, command);
            }
        }
    }

    @Override
    protected void playerConnected(int playerID) {
        System.out.println("Player connected: " + playerID);
        state.addPlayer(playerID);

        if (state.getPlayerCount() == 1) {
            sendToAll("Waiting for another player to join...");
        } else if (state.getPlayerCount() == 2) {
            sendToAll("Two players connected. Starting the game!");
            sendToAll(state);
            startGame();
        }
    }

    @Override
    protected void playerDisconnected(int playerID) {
        System.out.println("Player disconnected: " + playerID);
        state.removePlayer(playerID);
        sendToAll(state);
        if (state.getPlayerCount() < 2) {
            sendToAll("Player " + playerID + " disconnected. Waiting for another player to continue the game.");
            initializeNewGame();
        }
        synchronized (answersReceived) {
            answersReceived.remove(playerID);
        }
    }

    private void startGame() {
        currentQuestionIndex = -1;
        nextQuestion();
    }

    private void handleAnswer(int playerID, String answer) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            synchronized (answersReceived) {
                if (!answersReceived.containsKey(playerID)) {
                    answersReceived.put(playerID, answer);
                    System.out.println("Player " + playerID + " answered: " + answer);
                    sendToAll("Player " + playerID + " answered: " + answer);

                    sendToOne(playerID, "Player " + playerID + " has answered. Waiting for all players to answer...");

                    if (answersReceived.size() == state.getPlayerCount()) {
                        System.out.println("All players have answered.");
                        sendToAll("All players have answered.");
                        evaluateAnswers();
                        sendToAll(state);
                    }
                }
            }
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex >= questions.size()) {
            endGame();
            return;
        }

        Cartoon2000sTriviaGameQuestion currentQuestion = questions.get(currentQuestionIndex);
        sendToAll("Question: " + currentQuestion.question());
    }

    private void evaluateAnswers() {
        Cartoon2000sTriviaGameQuestion currentQuestion = questions.get(currentQuestionIndex);

        for (Map.Entry<Integer, String> entry : answersReceived.entrySet()) {
            int playerID = entry.getKey();
            String answer = entry.getValue();

            if (currentQuestion.isCorrectAnswer(answer)) {
                state.incrementScore(playerID);
                sendToAll("Player " + playerID + " answered correctly! The answer was: " + currentQuestion.answer());
            } else {
                sendToAll("Player " + playerID + " answered incorrectly.");
            }
        }
        answersReceived = new HashMap<>();
        nextQuestion();
    }

    private void endGame() {
        if (!state.hasAnyPlayerScored()) {
            sendToAll("The game ended with no correct answers. Better luck next time!");
        } else {
            int winner = state.getWinner();
            if (winner == -1) {
                sendToAll("The game ended in a tie!");
            } else {
                sendToAll("Player " + winner + " wins the game!");
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Cartoon2000sTriviaGameServer();
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
