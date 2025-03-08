// Tyler Johnson 
// February 22nd, 2025
// Tjj29@njit.edu 
// IT114 - 004
// Phase 1 Assignment: Server and Multi-Clients

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class Cartoon2000sTriviaGameServer extends Hub {

    private static final int PORT = 37829;
    private Cartoon2000sTriviaGameState state;
    private HashSet<Integer> playerIDs;
    private Cartoon2000sTriviaGameQuestionList questions;
    private int currentQuestionIndex = -1;
    private Map<Integer, String> answersReceived;

    public Cartoon2000sTriviaGameServer(int port) throws IOException {
        super(PORT);
        setAutoreset(true);
        state = new Cartoon2000sTriviaGameState();
        playerIDs = new HashSet<>();
        questions = new Cartoon2000sTriviaGameQuestionList();
        answersReceived = new HashMap<>();
        System.out.println("Server started on port " + PORT);
    }

    @Override
    protected void messageReceived(int playerID, Object message) {
        if (message instanceof String) {
            String command = ((String) message).trim();

            if (command.equalsIgnoreCase("restart")) {
                if (playerIDs.size() >= 2) {
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
    protected void playerDisconnected(int playerID) {
        System.out.printf("Player %d has left the game.\n", playerID);
        sendToAll("Player " + playerID + " has left the game.");
        playerIDs.remove(playerID);
        answersReceived.remove(playerID);
        sendToAll(state);
    }
    
    @Override
    protected void playerConnected(int playerID) {
        if (playerIDs.contains(playerID)) {
            System.out.printf("Player %d has rejoined the game.\n", playerID);
            sendToAll("Player " + playerID + " has rejoined.");
        } else {
            System.out.printf("Player %d has joined the game.\n", playerID);
            sendToAll("Player " + playerID + " has joined.");
        }
    
        playerIDs.add(playerID);
        sendToAll(state);
    }

    private void initializeNewGame() {
        state.clearScores();
        currentQuestionIndex = -1;
        answersReceived.clear();
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
                    sendToAll("Player " + playerID + " answered: " + answer);
                    if (answersReceived.size() == playerIDs.size()) {
                        sendToAll("All players have answered.");
                        evaluateAnswers();
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
        answersReceived.clear();
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
        sendToAll("Type 'restart' to play again.");
    }

    public static void main(String[] args) {
        try {
            new Cartoon2000sTriviaGameServer(PORT);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
