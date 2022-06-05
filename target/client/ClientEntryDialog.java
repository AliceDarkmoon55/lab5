package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import xmlMessages.ErrorMessage;
import xmlMessages.Message;
import xmlMessages.ServerConfirmMessage;

public class ClientEntryDialog extends JDialog {
    public ClientEntryDialog(String title, boolean modal, final Client client) {
        super(client, title, modal);
        this.setBounds(600, 200, 600, 130);
        this.setLayout(new FlowLayout(1, 5, 5));
        JLabel nameLabel = new JLabel("Enter your name");
        JTextField nameField = new JTextField();
        JButton loginButton = new JButton("Connect");
        JLabel infoLabel = new JLabel();
        nameLabel.setPreferredSize(new Dimension(120, 30));
        nameField.setPreferredSize(new Dimension(150, 30));
        loginButton.setPreferredSize(new Dimension(100, 30));
        infoLabel.setPreferredSize(new Dimension(150, 60));
        this.add(nameLabel);
        this.add(nameField);
        this.add(loginButton);
        this.add(infoLabel);
        loginButton.addActionListener((e) -> {
            if (!nameField.getText().trim().isEmpty()) {
                client.setUserName(nameField.getText());
                client.setTitle(nameField.getText() + "'s chat");
                client.sendMessage(client.getConstructor().createLoginMessage(client.getUserName()));

                while(!client.getInputData().hasNext()) {
                }

                Message message = client.getParser().parseCommand(client.getInputData().nextLine());
                if (message.getType().equals("error")) {
                    ErrorMessage typedMessage = (ErrorMessage)message;
                    infoLabel.setText(typedMessage.getReason());
                } else if (message.getType().equals("connection")) {
                    ServerConfirmMessage typedMessagex = (ServerConfirmMessage)message;
                    client.setSessionID(typedMessagex.getSessionId());
                    this.dispose();
                }
            }

        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.sendMessage(client.getConstructor().createRefuseMessage());
                System.exit(1);
            }
        });
        this.setVisible(true);
    }
}
