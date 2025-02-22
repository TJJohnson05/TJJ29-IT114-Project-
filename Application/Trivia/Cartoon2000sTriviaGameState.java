import java.io.Serializable;

public class Cartoon2000sTriviaGameState implements Serializable {

    private static final long serialVersionUID = 1L;
    public String message; // Message sent by a client
    public int senderID; // ID of the player sending the message

    public synchronized void applyMessage(int sender, Object message) {
        if (message instanceof String) {
            this.senderID = sender;
            this.message = (String) message;
        }
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
