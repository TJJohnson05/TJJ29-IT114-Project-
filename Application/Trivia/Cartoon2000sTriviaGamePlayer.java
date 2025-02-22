

import java.io.IOException;
import java.util.Scanner;

/**
 * A command-line version of the MoviesTriviaGamePlayer class that connects to a
 * trivia game server and communicates using standard input/output.
 */
public class Cartoon2000sTriviaGamePlayer {

    private static final int PORT = 37829; // Port number for the server.

    private static volatile boolean connected = false; // Tracks connection status.
    private static MoviesTriviaGameClient moviesTriviaGameClient;

    public static void main(String[] args) {
        String host = "";
        Scanner scanner = new Scanner(System.in);
        if (args.length == 0) {
            System.out.print("Enter the host name of the computer hosting the trivia game: ");
            host = scanner.nextLine().trim();
        } else {
            host = args[0];
        }

        if (host.isEmpty()) {
            System.out.println("Host name cannot be empty. Exiting.");
            scanner.close();
            return;
        }

        // Try to establish a connection to the server.
        try {
            System.out.println("Connecting to " + host + "...");
            moviesTriviaGameClient = new MoviesTriviaGameClient(host);
            connected = true;
            System.out.println("Connected to the server. Type your messages below. Type 'quit' to exit.");
        } catch (IOException e) {
            System.out.println("Failed to connect to the server: " + e.getMessage());
            scanner.close();
            return;
        }

        // Main loop for sending messages.
        while (connected) {
            String message = scanner.nextLine().trim();

            if (message.equalsIgnoreCase("quit")) {
                doQuit();
                break;
            }

            if (!message.isEmpty()) {
                moviesTriviaGameClient.send(message);
            }
        }

        scanner.close();
    }

    /**
     * Handles clean disconnection from the server.
     */
    private static void doQuit() {
        if (connected) {
            moviesTriviaGameClient.disconnect();
            try {
                Thread.sleep(1000); // Time for DisconnectMessage to actually be sent.
            } catch (InterruptedException e) {
            }
            connected = false;
            System.out.println("Disconnected from the server. Goodbye!");
        }
    }
    
        public int senderID;
        
                public String message;
            
                /**
                 * Inner class representing the trivia game client.
                 */
                private static class MoviesTriviaGameClient extends Client {
            
                    /**
                     * Constructor to create a client connection to the specified host.
                     *
                     * @param host The server's host name or IP address.
                     * @throws IOException If the connection cannot be established.
                     */
                    MoviesTriviaGameClient(String host) throws IOException {
                        super(host, PORT);
                    }
            
                    /**
                     * Called when a message is received from the server.
                     *
                     * @param message The received message.
                     */
                    @Override
                    protected void messageReceived(Object message) {
                        if (message instanceof Cartoon2000sTriviaGamePlayer) {
                            Cartoon2000sTriviaGamePlayer state = (Cartoon2000sTriviaGamePlayer) message;
                            if (state.senderID != 0) {
                            System.out.println("Player  " + state.senderID + " " + state.message);
                }
            }
        }

        /**
         * Called when the connection is closed due to an error.
         *
         * @param message Error message describing the reason for disconnection.
         */
        @Override
        protected void connectionClosedByError(String message) {
            System.out.println("Connection closed due to error: " + message);
            connected = false;
        }

        /**
         * Called when a new player connects to the server.
         *
         * @param newPlayerID The ID of the newly connected player.
         */
        @Override
        protected void playerConnected(int newPlayerID) {
            System.out.println("Player " + newPlayerID + " joined the game.");
        }

        /**
         * Called when a player disconnects from the server.
         *
         * @param departingPlayerID The ID of the player who disconnected.
         */
        @Override
        protected void playerDisconnected(int departingPlayerID) {
            System.out.println("Player " + departingPlayerID + " left the game.");
        }
    }
}