/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-09
 */
package chatting.graphics;

import javax.swing.*;
import java.awt.*;

public class Server {
    private JFrame mainFrame = new JFrame();
    private JPanel contentPanel = new JPanel();
    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();
    private chatting.communication.Server server;
    private JLabel label = new JLabel();

    private String imagePath = "resource/image/cat.jpg";


    public Server() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        contentPanel.setBackground(Color.BLACK);
//        contentPanel.add(textArea, BorderLayout.CENTER);
//        contentPanel.add(textField, BorderLayout.SOUTH);

        JButton button  = new JButton("button");
        label.setOpaque(false);
        label.setIcon(new ImageIcon(imagePath));
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.add(button, BorderLayout.SOUTH);

        mainFrame.add(contentPanel);

        textField.addActionListener(event -> sendToAll());
        button.addActionListener(event -> sendImageToAll());

        server = new chatting.communication.Server(this::displayDataFromClient,this::displayImage);
        server.startAccept();


        mainFrame.setTitle("Server");
        mainFrame.setSize(600, 600);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
    public void displayDataFromClient(String msg){
        textArea.append("Client : "+msg+"\r\n");
    }
    public void sendToAll(){
        String msg = textField.getText();
        textField.setText("");
        server.sentToAllClient(msg);
    }
    public void displayImage(ImageIcon image){
        label.setIcon(image);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    public void sendImageToAll(){
        server.sendImageToClients(label);
    }
}
