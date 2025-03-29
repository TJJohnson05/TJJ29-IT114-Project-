package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class Cartoon2000sTriviaGameMainPanel extends JPanel {

    private static final Color BLUE = Color.decode("#3b639e");
    private static final Color DARK_BLUE = Color.decode("#214276");
    private static final Color GOLD = Color.decode("#f1dfa9");

    private Cartoon2000sTriviaGameMainPanel window;

    private Cartoon2000sTriviaGameStatusPanel statusPanel;
    private Cartoon2000sTriviaGameMessagePanel messagePanel;
    private Cartoon2000sTriviaGameScoreBoardPanel scoreBoardPanel;

    private JLabel answerLabel;
    private JTextField answerText;
    private JButton sendButton;
    private JButton restartButton;
    private JButton quitButton;

    public Cartoon2000sTriviaGameMainPanel(Cartoon2000sTriviaGameMainPanel window) {
        super();
        this.window = window;
        initialize();
    }


    private void initialize() {
        this.setBackground(Color.BLACK);
        this.setLayout(new BorderLayout(2, 2));

        statusPanel = new Cartoon2000sTriviaGameStatusPanel();
        this.add(statusPanel, BorderLayout.NORTH);

        scoreBoardPanel = new Cartoon2000sTriviaGameScoreBoardPanel(); // Removed redeclaration
        this.add(scoreBoardPanel, BorderLayout.EAST);

        messagePanel = new Cartoon2000sTriviaGameMessagePanel();
        this.add(messagePanel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(DARK_BLUE);

        answerLabel = new JLabel("Answer:");
        answerLabel.setForeground(GOLD);
        answerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(answerLabel);

        answerText = new JTextField(40);
        controlPanel.add(answerText);

        sendButton = new JButton("Send");
        sendButton.setForeground(BLUE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(sendButton);

        controlPanel.add(Box.createHorizontalStrut(30));
        restartButton = new JButton("Restart");
        restartButton.setForeground(BLUE);
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(restartButton);
        quitButton = new JButton("Quit");
        quitButton.setForeground(BLUE);
        quitButton.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(quitButton);

        controlPanel.add(quitButton);
        answerText.addActionListener(this::sendAnswer);
        sendButton.addActionListener(this::sendAnswer);
        restartButton.addActionListener(this::restartGame);
        quitButton.addActionListener(this::quitGame);

        this.add(controlPanel, BorderLayout.SOUTH);

        this.setDisable();
    }

    public void setDisable() {
        answerText.setEditable(false);
        answerText.setEnabled(false);
        answerText.setBackground(Color.LIGHT_GRAY);
        answerText.setText("");
        sendButton.setEnabled(false);
        restartButton.setEnabled(false);
    }

    public void setEnable() {
        answerText.setEditable(true);
        answerText.setEnabled(true);
        answerText.setBackground(Color.WHITE);
        answerText.requestFocus();
        sendButton.setEnabled(true);
        restartButton.setEnabled(true);
    }

    public void setServerConnected(int playerID) {
        statusPanel.updateServerConnected(playerID);
    }

    public void setServerDisconnected() {
        statusPanel.updateServerDisconnected();
        messagePanel.clearText();
        scoreBoardPanel.resetPlayers(new HashMap<>());
    }

    public void setMessage(String message) {
        messagePanel.setText(message);
    }

    public void updateScoreBoard(HashMap<Integer, Integer> playerScores) {
        scoreBoardPanel.resetPlayers(playerScores);
    }

    private void sendAnswer(ActionEvent event) {
        String message = answerText.getText();
        if (message.trim().length() == 0)
            return;
        window.send(message);
                answerText.setText("");
                answerText.requestFocus();
            }
        
            private void send(String message) {
            
              throw new UnsupportedOperationException("Unimplemented method 'send'");
            }
        
            private void restartGame(ActionEvent event) {
        window.send("restart");
    }
    public void doQuit() {
      System.exit(0);  // This will terminate the program
  }
  
    private void quitGame(ActionEvent event) {
        window.doQuit();
      
    }
        
                    public static void main(String[] args) {
        JFrame frame = new JFrame("Cartoon 2000s Trivia Game - Main Panel");

        Cartoon2000sTriviaGameMainPanel panel = new Cartoon2000sTriviaGameMainPanel(null); // Pass null if necessary
        frame.add(panel);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
