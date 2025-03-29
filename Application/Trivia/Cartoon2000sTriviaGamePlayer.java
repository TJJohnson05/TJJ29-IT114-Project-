// Tyler Johnson
// March 7th, 2025
// Tjj29@njit.edu
// IT114 - 004
// Phase 2 Assignment: Trivia Game Flow



import java.io.IOException;
import java.util.Scanner;
import java.io.ObjectInputStream;


public class Cartoon2000sTriviaGamePlayer {

    private static final int PORT = 37829; // Server port
    private static volatile boolean connected = false;
    private static Cartoon2000sTriviaGameClient client;
    private static ObjectInputStream in;
    private static String host = "";
    private static int playerID = -1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (args.length == 0) {
            System.out.print("Enter the host name of the trivia game server: ");
            host = scanner.nextLine().trim();
        } else {
            host = args[0];
        }

        if (host.isEmpty()) {
            System.out.println("Host name cannot be empty. Exiting.");
            scanner.close();
            return;
        }

        // Try to connect to the server
        connectToServer(scanner);

        scanner.close();
    }

    /**
     * Establishes a connection to the trivia game server.
     */
    private static void connectToServer(Scanner scanner) {
        try {
            System.out.println("Connecting to " + host + "...");
            client = new Cartoon2000sTriviaGameClient(host, playerID);
            connected = true;
            System.out.println("Connected to the server. Type messages below. Type 'quit' to exit or 'reconnect' to reconnect.");

            // Start a separate thread to receive messages from the server
            Thread receiveThread = new Thread(() -> {
                while (connected) {
                    client.receiveMessages();
                }
            });
            receiveThread.start();

            // Main loop for sending messages
            while (connected) {
                String message = scanner.nextLine().trim();

                if (message.equalsIgnoreCase("quit")) {
                    doQuit();
                    break;
                }

                if (message.equalsIgnoreCase("reconnect")) {
                    reconnectToServer();
                    break;
                }

                if (!message.isEmpty()) {
                    client.send(message);
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to connect to the server: " + e.getMessage());
        }
    }

    /**
     * Disconnects from the server gracefully.
     */
    private static void doQuit() {
        if (connected) {
            client.disconnect();
            try {
                Thread.sleep(1000); // Give time for disconnect message to be sent
            } catch (InterruptedException ignored) {}
            connected = false;
            System.out.println("Disconnected from the server. Goodbye!");
        }
    }

    /**
     * Attempts to reconnect to the server using the same player ID.
     */
    private static void reconnectToServer() {
        try {
            System.out.print("Reconnecting to the server... ");
            client = new Cartoon2000sTriviaGameClient(host, playerID);
            connected = true;
            System.out.println("Reconnected to the server.");
        } catch (IOException e) {
            System.out.println("Failed to reconnect: " + e.getMessage());
        }
    }

    /**
     * Inner class representing the trivia game client.
     */
    private static class Cartoon2000sTriviaGameClient extends Client {
        private final int playerID;

        /**
         * Constructor for the client.
         *
         * @param host     The server's host name or IP address.
         * @param playerID The player's ID.
         * @throws IOException If the connection cannot be established.
         */
        Cartoon2000sTriviaGameClient(String host, int playerID) throws IOException {
            super(host, PORT);
            this.playerID = playerID;
            System.out.println("Connected with player ID: " + playerID);
            sendPlayerIDToServer();
        }

        /**
         * Sends the player ID to the server for tracking.
         */
        private void sendPlayerIDToServer() {
            send("RECONNECT " + playerID);
        }

        /**
         * Handles received messages from the server.
         *
         * @param message The received message.
         */
        @Override
        protected void messageReceived(Object message) {
            if (message instanceof String) {
                System.out.println(message);
            } else if (message instanceof Cartoon2000sTriviaGameState) {
                Cartoon2000sTriviaGameState state = (Cartoon2000sTriviaGameState) message;
                System.out.println("Player " + state.getSenderID() + ": " + state.getMessage());
            }
        }

        /**
         * Handles errors that close the connection.
         *
         * @param message The error message.
         */
        @Override
        protected void connectionClosedByError(String message) {
            System.out.println("Connection closed due to error: " + message);
            connected = false;
            System.exit(0);
        }

        /**
         * Handles new player connections.
         *
         * @param newPlayerID The ID of the new player.
         */
        @Override
        protected void playerConnected(int newPlayerID) {
            System.out.println("Player " + newPlayerID + " joined the game.");
        }

        /**
         * Handles player disconnections.
         *
         * @param departingPlayerID The ID of the departing player.
         */
        @Override
        protected void playerDisconnected(int departingPlayerID) {
            System.out.println("Player " + departingPlayerID + " left the game.");
        }

        /**
         * Listens for incoming messages from the server.
         */
        public void receiveMessages() {
            try {
                Object message = in.readObject();
                messageReceived(message);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error receiving message: " + e.getMessage());
            }
        }
    }
}
