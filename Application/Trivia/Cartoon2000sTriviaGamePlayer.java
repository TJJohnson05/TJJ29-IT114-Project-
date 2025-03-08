// Tyler Johnson 
// Feburary 22nd 2025
// Tjj29@njit.edu 
// IT114 - 004
// Phase 1 Assignment: Server and Multi-Clients

import java.io.IOException;
import java.util.Scanner;
import java.io.ObjectInputStream;

public class Cartoon2000sTriviaGamePlayer {

    private static final int PORT = 37829; // Port number for the server.
    private static volatile boolean connected = false; // Tracks connection status.
    private static Cartoon2000sTriviaGameClient client;
    private static ObjectInputStream in;
    private static String host = "";
    private static int playerID = -1;

    public static void main(String[] args) {
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
            client = new Cartoon2000sTriviaGameClient(host, playerID);
            connected = true;
            System.out.println("Connected to the server. Type your messages below. Type 'quit' to exit or 'reconnect' to reconnect.");

            // Start a new thread to handle receiving messages
            Thread receiveThread = new Thread(() -> {
                while (connected) {
                    client.receiveMessages(); // Make sure this method handles receiving messages from the server
                }
            });
            receiveThread.start();

            // Main loop for sending messages
            while (connected) {
                String message = scanner.nextLine().trim();

                if (message.equalsIgnoreCase("quit")) {
                    doQuit(); // Disconnect and quit
                    break;
                }

                if (message.equalsIgnoreCase("reconnect")) {
                    reconnectToServer(); // Reconnect after quitting
                    break;
                }

                if (!message.isEmpty()) {
                    client.send(message); // Send the message to the server
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to connect to the server: " + e.getMessage());
            scanner.close();
            return;
        }

        scanner.close();
    }

    /**
     * Handles clean disconnection from the server.
     */
    private static void doQuit() {
        if (connected) {
            client.disconnect();
            try {
                Thread.sleep(1000); // Time for DisconnectMessage to actually be sent
            } catch (InterruptedException e) {}
            connected = false;
            System.out.println("Disconnected from the server. Goodbye!");
        }
    }

    /**
     * Reconnect to the server with the same player ID.
     */
    private static void reconnectToServer() {
        try {
            System.out.print("Reconnecting to the server... ");
            // Assuming you still have the previous `playerID` saved (you could prompt the user for it)
            client = new Cartoon2000sTriviaGameClient(host, playerID);
            connected = true;
            System.out.println("Reconnected to the server.");
        } catch (IOException e) {
            System.out.println("Failed to reconnect to the server: " + e.getMessage());
        }
    }

    /**
     * Inner class representing the trivia game client.
     */
    private static class Cartoon2000sTriviaGameClient extends Client {

        private int playerID;

        /**
         * Constructor to create a client connection to the specified host.
         *
         * @param host The server's host name or IP address.
         * @param playerID The player's ID.
         * @throws IOException If the connection cannot be established.
         */
        Cartoon2000sTriviaGameClient(String host, int playerID) throws IOException {
            super(host, PORT);
            this.playerID = playerID;
            System.out.println("Connected with player ID: " + playerID);
            sendPlayerIDToServer(playerID);
        }

        /**
         * Send the player ID to the server for identification purposes.
         */
        private void sendPlayerIDToServer(int playerID) {
            send("RECONNECT " + playerID);
        }

        /**
         * Called when a message is received from the server.
         *
         * @param message The received message.
         */
        @Override
        protected void messageReceived(Object message) {
            if (message instanceof String) {
                System.out.println(message); // Print received string messages
            } else if (message instanceof Cartoon2000sTriviaGameState) {
                Cartoon2000sTriviaGameState state = (Cartoon2000sTriviaGameState) message;
                System.out.println("Player " + state.getSenderID() + ": " + state.getMessage());
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
            System.exit(0); // Should end if server is shut down
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

        /**
         * Receive messages from the server (running in a separate thread).
         */
        public void receiveMessages() {
            try {
                // Assuming you have an input stream `in` connected to the server
                Object message = in.readObject(); // Read the message object from the server

                // Handle the received message
                messageReceived(message);
            } catch (IOException | ClassNotFoundException e) {
                // Log the error or handle it as necessary
                System.out.println("Error receiving message: " + e.getMessage());
            }
        }
    }
}
