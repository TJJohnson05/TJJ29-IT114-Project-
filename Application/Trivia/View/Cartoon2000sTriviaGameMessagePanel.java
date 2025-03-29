package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class Cartoon2000sTriviaGameMessagePanel extends JPanel {

    private static final Color GOLD = Color.decode("#f1dfa9");
    private static final Color DARK_RED = Color.decode("#ca1818");
    private static final String IMAGE_FILENAME = "/trivia/resources/images/cartoon_trivia_game.jpg";

    JTextArea textArea;
    JPanel textPanel;

    public Cartoon2000sTriviaGameMessagePanel() {
        this.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(getClass().getResource(IMAGE_FILENAME));
        if (imageIcon.getImage() == null) {
            System.err.println("Image not found: " + IMAGE_FILENAME);
        }
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(686, 386, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
        imageLabel.setLayout(new BorderLayout());

        textArea = new JTextArea(6, 18);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(GOLD);
        textArea.setFont(new Font("Arial", Font.BOLD, 18));

        BevelBorder customBorder = (BevelBorder) BorderFactory.createBevelBorder(
                BevelBorder.RAISED,
                GOLD,
                DARK_RED);

        textPanel = new JPanel();
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(customBorder);
        textPanel.add(textArea);

        imageLabel.add(textPanel);

        this.add(imageLabel, BorderLayout.CENTER);
        this.clearText();
    }

    public void clearText() {
        textPanel.setVisible(false);
        textArea.setText("");
        revalidate();
        repaint();
    }

    public void setText(String text) {
        if (textArea != null) {
            textPanel.setVisible(true);
            textArea.setText(text);
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Cartoon 2000s Trivia Game - Message Panel");

        Cartoon2000sTriviaGameMessagePanel panel = new Cartoon2000sTriviaGameMessagePanel();
        frame.add(panel);
        frame.pack();
        frame.setSize(686, 386);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread.sleep(5000);
        panel.setText("Cartoon trivia question here.");
        Thread.sleep(5000);
        panel.setText("Next question here.");
        Thread.sleep(5000);
        panel.clearText();
        Thread.sleep(5000);
        panel.setText("Game Over");
    }
}
