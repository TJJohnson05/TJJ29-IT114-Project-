import java.io.IOException;
import java.util.HashSet;

public class Cartoon2000sTriviaGameServer extends Hub {

    private static final int PORT = 37829;
    private Cartoon2000sTriviaGameState state;
    private HashSet<Integer> playerIDs;

    public Cartoon2000sTriviaGameServer(int port) throws IOException {
        super(PORT);
        setAutoreset(true);
        state = new Cartoon2000sTriviaGameState();
        playerIDs = new HashSet<>();
        System.out.println("Server started on port " + PORT);
    }

    @Override
    protected void messageReceived(int playerID, Object message) {
        System.out.printf("Player %d: %s\n", playerID, message);
        state.applyMessage(playerID, message);
        sendToAll(state);
    }

    @Override
    protected void playerDisconnected(int playerID) {
        System.out.printf("Player %d has left the game.\n", playerID);
        sendToAll("Player " + playerID + " has left the game.");
        playerIDs.remove(playerID); // Remove player from active list
        sendToAll(state); // Send updated game state to remaining players
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
    
        playerIDs.add(playerID); // Add player to the active list
        sendToAll(state); // Send the current game state to all players
    }

    public static void main(String[] args) {
        try {
            new Cartoon2000sTriviaGameServer(PORT);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
