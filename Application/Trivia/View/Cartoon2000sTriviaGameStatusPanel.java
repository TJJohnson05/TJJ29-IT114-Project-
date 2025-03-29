package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Cartoon2000sTriviaGameStatusPanel extends JPanel {

  private static final Color GOLD = Color.decode("#f1dfa9");
  private static final Color DARK_BLUE = Color.decode("#214276");

  private JLabel serverStatus;
  
  public Cartoon2000sTriviaGameStatusPanel() {
    super();
    initialize();
  }

  private void initialize() {
    this.setBackground(DARK_BLUE);
    this.setBorder(new EmptyBorder(5, 5, 5, 5));
    this.setLayout(new BorderLayout());

    serverStatus = new JLabel();
    serverStatus.setFont(new Font("Arial", Font.BOLD, 16));

    this.add(serverStatus, BorderLayout.WEST);
    this.updateServerDisconnected();
  }

  public void updateServerConnected(int playerID) {
    serverStatus.setText("Player " + playerID + " is ready!");
    serverStatus.setForeground(GOLD);
  }

  public void updateServerDisconnected() {
    serverStatus.setText("Connection lost! Try again.");
    serverStatus.setForeground(Color.RED);
  }

  public static void main(String[] args) throws InterruptedException {
    JFrame frame = new JFrame("Cartoon 2000s Trivia Game - Status Panel");

    Cartoon2000sTriviaGameStatusPanel panel = new Cartoon2000sTriviaGameStatusPanel();
    frame.add(panel);
    frame.pack();
    frame.setSize(700, 55);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    Thread.sleep(5000);
    panel.updateServerConnected(10);
    Thread.sleep(5000);
    panel.updateServerDisconnected();
  }
}
