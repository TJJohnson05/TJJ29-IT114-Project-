

import java.io.Serializable;


public class Cartoon2000sTriviaGameState implements Serializable {


   public String message; // Original message from a client.
   public int senderID; // The ID of the client who sent that message.


   public void applyMessage(int sender, Object message) {
       this.senderID = sender;
       this.message = (String) message;
   }
}
