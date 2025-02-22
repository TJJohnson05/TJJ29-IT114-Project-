
import java.io.IOException;
import Application.netgame.common.*;


public class Cartoon2000sTriviaGameServer extends Hub {
    private final static int PORT = 37829;
    private Cartoon2000sTriviaGameState state;
   
   public Cartoon2000sTriviaGameServer(int port) throws IOException {
           super(PORT);
           setAutoreset(true);
       state = new Cartoon2000sTriviaGameState();
       System.out.println("Server started on port " + PORT);
   }
   /**
    * Handles messages received from clients by applying them to the game state
    * and broadcasting the updated state to all connected players.
    */
    @Override
    protected void messageReceived(int playerID, Object message) {
     System.out.printf("Player %d %s\n", playerID, message);
     (state).applyMessage(playerID, message);
          sendToAll(state);
     }
     
     /**
 * Called when a player connects. Updates the game state and notifies all
 * players.
 */
@Override
protected void playerConnected(int playerID) {
    System.out.printf("Player %d has joined the game.\n", playerID);
    sendToAll("Player " + playerID + " has joined.");
    sendToAll(state);
}
/**
 * Called when a player disconnects. Updates the game state and notifies
 * remaining players.
  */
  @Override
  protected void playerDisconnected(int playerID) {
      System.out.printf("Player %d has left the game.\n", playerID);
      sendToAll("Player " + playerID + "has left.");
      sendToAll(state);
  }


  public static void main(String[] args) {
      try {
          new Cartoon2000sTriviaGameServer(PORT);
      } catch (IOException e) {
          System.out.println("Error starting server: " + e.getMessage());
      }
  }
  
}
