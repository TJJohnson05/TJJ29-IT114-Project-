
import java.io.IOException;
import Application.netgame.common.*;


public class Cartoon2000sTriviaGameServer extends Hub {

   
   
   public Cartoon2000sTriviaGameServer(int port) throws IOException {
           super(port);
           setAutoreset(true);
       state = new Cartoon2000sTriviaGameState();
   }
           //TODO Auto-generated constructor stub
       }
   
   
   private final static int PORT = 37829;


   private Cartoon2000sTriviaGameState state;

   /**
    * Handles messages received from clients by applying them to the game state
    * and broadcasting the updated state to all connected players.
    */
    @Override
    protected void messageReceived(int playerID, Object message) {
     System.out.printf("Player %d %s\n", playerID, message);
     state.applyMessage(playerID, message);
     sendToAll(state);
}


/**
 * Called when a player connects. Updates the game state and notifies all
 * players.
 */
@Override
protected void playerConnected(int playerID) {
    System.out.printf("Player %d connected.\n", playerID);
    sendToAll(state);
}


/**
 * Called when a player disconnects. Updates the game state and notifies
 * remaining players.


  */
  @Override
  protected void playerDisconnected(int playerID) {
      System.out.printf("Player %d disconnected.\n", playerID);
      sendToAll(state);
  }


  public static void main(String[] args) {
      try {
          new Cartoon2000sTriviaGameState();
      } catch (IOException e) {
          System.out.println("Error starting server: " + e.getMessage());
      }
  }
}
