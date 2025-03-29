
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


import View.Cartoon2000sTriviaGameMainPanel;
import View.Cartoon2000sTriviaGameScoreBoardPanel;

/**
 * A GUI version of the Cartoon2000sTriviaGamePlayer class that connects to a
 * trivia game server and communicates using Java Swing/AWT libraries.
 */
public class Cartoon2000sTriviaGamePlayerWindow extends JFrame {

    private static final int PORT = 37829; // Port number for the server.

    private static volatile boolean connected = false; // Tracks connection status.

    // Represents the connection to the Hub; used to send messages;
    // also receives and processes messages from the Hub.
    private static Cartoon2000sTriviaGameClient cartoon2000sTriviaGameClient;

    // Represents the GUI panel containing all buttons/etc
    private Cartoon2000sTriviaGameMainPanel mainPanel;

    public static void main(String[] args) {
        String host = "";
        if (args.length == 0) {
            host = JOptionPane.showInputDialog(
                null,
                "Enter the host name of the\ncomputer hosting the 2000s cartoon trivia game:", 
                "Cartoon 2000s Trivia Game",
                JOptionPane.QUESTION_MESSAGE);
        } else {
            host = args[0];
        }

        if (host.isEmpty()) {
            System.out.println("Host name cannot be empty. Exiting.");
            return;
        }
        new Cartoon2000sTriviaGamePlayerWindow(host);
    }

    /**
     * Called when the user clicks the Quit button or closes
     * the window by clicking its close box.
     */
    public void doQuit() {
        if (connected) {
            mainPanel.setServerDisconnected();
            cartoon2000sTriviaGameClient.disconnect();
        }
        this.dispose();
        try {
            Thread.sleep(1000); // Time for DisconnectMessage to actually be sent.
        } catch (InterruptedException e) {
        }
        connected = false;
        System.out.println("Disconnected from the server. Goodbye!");
    }

    /**
     * A Client connects to the Hub and is used to send messages to
     * and receive messages from a Hub.
     */
    private class Cartoon2000sTriviaGameClient extends Client {

        /**
         * Opens a connection to the server on a specified computer.
         */
        Cartoon2000sTriviaGameClient(String host) throws IOException {
            super(host, PORT);
        }

        /**
         * Called when a message is received from the server.
         *
         * @param message The received message.
         */
        @Override
        protected void messageReceived(Object message) {
            if (message instanceof Cartoon2000sTriviaGameState) {
                Cartoon2000sTriviaGameState state = (Cartoon2000sTriviaGameState) message;
                mainPanel.updateScoreBoard(state.playerScores);
                if (state.senderID != 0) {
                    System.out.println("Player " + state.senderID + ": " + state.message);
                }
            } else if (message instanceof String) {
                System.out.println(message.toString());
                mainPanel.setMessage(message.toString());
            }
        }

        /**
         * Called when the connection to the client is shut down because of some
         * error message.
         */
        @Override
        protected void connectionClosedByError(String message) {
            System.out.println("Connection closed due to error: " + message);
            mainPanel.setServerDisconnected();
            mainPanel.setDisable();
            connected = false;
            cartoon2000sTriviaGameClient = null;
        }

        /**
         * Posts a message when someone joins the game.
         */
        @Override
        protected void playerConnected(int newPlayerID) {
            System.out.println("Player " + newPlayerID + " joined the game.");
        }

        /**
         * Posts a message when someone leaves the game.
         */
        @Override
        protected void playerDisconnected(int departingPlayerID) {
            System.out.println("Player " + departingPlayerID + " left the game.");
        }
    }

    /**
     * Constructor creates the window and starts the process of connecting
     * to the server.
     * 
     * @param host The IP address or host name of the computer where the server is
     *             running.
     */
    private Cartoon2000sTriviaGamePlayerWindow(final String host) {
        this.setTitle("Cartoon 2000s Trivia Game");

        mainPanel = new Cartoon2000sTriviaGameMainPanel(this);
        this.add(mainPanel);
        this.pack();

        this.setLocation(200, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doQuit();
            }
        });

        // Creating a new thread for connecting to the server
        new Thread() {
            public void run() {
                try {
                    System.out.println("Connecting to " + host + "...");
                    cartoon2000sTriviaGameClient = new Cartoon2000sTriviaGameClient(host);
                    connected = true;
                    mainPanel.setEnable();
                    mainPanel.setServerConnected(cartoon2000sTriviaGameClient.getID());

                } catch (IOException e) {
                    System.out.println("Failed to connect to the server: " + e.getMessage());
                    System.out.println("Error: " + e);
                }
            }
        }.start();
    }

    public void send(String message) {
        cartoon2000sTriviaGameClient.send(message);
    }
}
