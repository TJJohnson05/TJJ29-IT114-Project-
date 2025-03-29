// Tyler Johnson 
// March 7th 2025
// Tjj29@njit.edu 
// IT114 - 004
// Phase 2 Assignment: Trivia Game Flow
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cartoon2000sTriviaGameState implements Serializable {

    private static final long serialVersionUID = 1L;
    public String message; // Message sent by a client
    public int senderID; // ID of the player sending the message
    HashMap<Integer, Integer> playerScores;

    public Cartoon2000sTriviaGameState() {
        playerScores = new HashMap<>();
    }

    public synchronized void applyMessage(int sender, Object message) {
        if (message instanceof String) {
            this.senderID = sender;
            this.message = (String) message;
        }
    }

    public void addPlayer(int playerID) {
        playerScores.put(playerID, 0);
    }

    public void removePlayer(int playerID) {
        playerScores.remove(playerID);
    }

    public int getPlayerCount() {
        return playerScores.size();
    }

    public void incrementScore(int playerID) {
        playerScores.put(playerID, playerScores.getOrDefault(playerID, 0) + 1);
    }

    public boolean hasAnyPlayerScored() {
        return playerScores.values().stream().anyMatch(score -> score > 0);
    }

    public int getWinner() {
        ArrayList<Integer> maxKeys = new ArrayList<>();
        int maxValue = Integer.MIN_VALUE;
        for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
            int value = entry.getValue();
            if (value > maxValue) {
                maxKeys.clear();
                maxKeys.add(entry.getKey());
                maxValue = value;
            } else if (value == maxValue) {
                maxKeys.add(entry.getKey());
            }
        }
        return maxKeys.size() > 1 ? -1 : maxKeys.get(0);
    }

    public void clearScores() {
        playerScores.replaceAll((_, _) -> 0);
    }

    @Override
    public String toString() {
        return "Player " + senderID + ": " + message;
    }

    public int getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }
}
