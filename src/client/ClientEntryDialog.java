package client;

import xmlMessages.ServerErrorAnswer;
import xmlMessages.Message;
import xmlMessages.ServerSuccessAnswer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientEntryDialog extends JDialog {
    public ClientEntryDialog(String title, boolean modal, Client client) {
        super(client, title, modal);

        setSize(500, 80);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setPreferredSize(new Dimension(110, 35));
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(160, 35));
        add(nameField);

        JButton connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(100, 35));
        add(connectButton);

        JLabel infoLabel = new JLabel();
        infoLabel.setPreferredSize(new Dimension(150, 60));
        add(infoLabel);

        connectButton.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty()) {

                client.setUserName(nameField.getText());
                client.setTitle("User: " + nameField.getText());

                client.sendMessage(client.getConstructor().createClientLoginMessage(client.getUserName()));

                while (true) {
                    if (client.getInputData().hasNext()) {
                        Message msg = client.getParser().parseCommand(client.getInputData().nextLine());

                        if (msg.getType().equals("error")) {
                            ServerErrorAnswer typedMsg = (ServerErrorAnswer) msg;
                            infoLabel.setText(typedMsg.getReason());
                            break;

                        } else if (msg.getType().equals("success")) {
                            ServerSuccessAnswer typedMsg = (ServerSuccessAnswer)msg;
                            client.setSessionID(typedMsg.getSessionID());
                            dispose();
                        }

                        break;
                    }
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.sendMessage(client.getConstructor().createRefuseMessage());
                System.exit(1);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
