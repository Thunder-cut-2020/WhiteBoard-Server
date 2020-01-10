package chatting.graphics;

import chatting.communication.Client;

import javax.swing.*;
import java.awt.*;

public class Client2GUI {
    private JFrame frame = new JFrame();
    private JPanel contentPanel = new JPanel();
    private JTextField textField = new JTextField();
    private JTextArea textArea = new JTextArea();
    private Client client;
    private Font serverFont = new Font("궁서",Font.BOLD,20);
    private Font clientFont = new Font("고딕",Font.BOLD,15);
    private JLabel label;
    private String imagePath = "resource/image/cat.jpg";

    public Client2GUI() {
        initialize();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        contentPanel.add(textArea, BorderLayout.CENTER);
//        contentPanel.add(textField, BorderLayout.SOUTH);
        textField.setFont(new Font("고딕", Font.BOLD, 15));
        textArea.setFont(new Font("고딕", Font.BOLD, 15));
        textArea.setEditable(false);
        contentPanel.setFocusable(true);

        label = new JLabel();
//        label.setIcon(client.getImage());
//        label.setIcon(new ImageIcon(imagePath));

        JButton button = new JButton("Click");

        contentPanel.add(button,BorderLayout.SOUTH);
        contentPanel.add(label,BorderLayout.CENTER);

        frame.setTitle("Client1");

//        textField.addActionListener(event -> userTextDisplaySend());
        button.addActionListener(event -> {
            client.sendImageToServer(label);
            System.out.println("Clicked");
        });
        frame.add(contentPanel);

        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initialize() {
        client = new Client(this::serverTextDisplay, this::displayImage);
        client.addDisplay(this::serverTextDisplay);
        client.run();
    }

    public void userTextDisplaySend() {
        String text = textField.getText();
        client.writeToServer(text);
        textField.setText("");
        textArea.setFont(clientFont);
        textArea.append(text + "\r\n");
    }

    public void serverTextDisplay(String text) {
        textArea.setFont(serverFont);
        textArea.append(text + "\r\n");
    }

    public String getUserInput(){
        return textField.getText();
    }

    public void displayImage(ImageIcon image){
        label.setIcon(image);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
