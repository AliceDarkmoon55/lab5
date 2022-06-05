package client;

import xmlHandlers.xmlMessageConstructor;
import xmlHandlers.xmlParser;
import xmlMessages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Client extends JFrame {
    public static void main(String[] args) {
        Client client = new Client();
    }

    private xmlParser parser = new xmlParser();
    private xmlMessageConstructor constructor = new xmlMessageConstructor();

    private Socket clientSocket;
    private static final String hostName = "localhost";
    private static final int hostPort = 8189;
    private Scanner inputData;
    private PrintWriter outputData;
    private LinkedList<String> usersList;
    private String sessionID;
    private String userName;
    private Thread messageHandler;

    public Client() {
        try {
            clientSocket = new Socket(hostName, hostPort);
            inputData = new Scanner(clientSocket.getInputStream());
            outputData = new PrintWriter(clientSocket.getOutputStream());
            usersList = new LinkedList<>();
        } catch (IOException e) {
            System.err.println("Error: " + e);
            System.exit(1);
        }

        setSize(800, 500);
        setLayout(new BorderLayout());

        JTextArea chat = new JTextArea();
        chat.setLineWrap(true);
        chat.setEditable(false);

        JScrollPane chatScrollPane = new JScrollPane(chat);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setPreferredSize(new Dimension(600, 400));
        add(chatScrollPane, BorderLayout.CENTER);

        JTextArea users = new JTextArea();
        JScrollPane usersScrollPane = new JScrollPane(users);
        users.setEditable(false);
        usersScrollPane.setPreferredSize(new Dimension(200, 400));
        usersScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        usersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(usersScrollPane, BorderLayout.EAST);

        JTextArea messageText = new JTextArea();
        JScrollPane messageTextScrollPane = new JScrollPane(messageText);
        messageTextScrollPane.setPreferredSize(new Dimension(600, 50));
        messageTextScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        messageTextScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(200, 50));

        Container sender = new Container();
        sender.setLayout(new BorderLayout());
        sender.add(messageTextScrollPane, BorderLayout.CENTER);
        sender.add(sendButton, BorderLayout.EAST);
        add(sender, BorderLayout.SOUTH);

        ClientEntryDialog entryDialog = new ClientEntryDialog("Login", true, this);

        usersList = getUsersList();
        updateUserList(users);

        sendButton.addActionListener(e -> {
            if (!messageText.getText().trim().isEmpty()) {
                String msg = constructor.createClientMessage(messageText.getText(), sessionID);
                sendMessage(msg);
                messageText.selectAll();
                messageText.replaceSelection("");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                messageHandler.interrupt();

                sendMessage(constructor.createClientLogoutMessage(sessionID));
                inputData.close();
                outputData.close();

                System.exit(1);
            }
        });

        startWorking(chat, users);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startWorking(JTextArea chat, JTextArea users) {
        messageHandler = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (inputData.hasNext()) {
                    Message msg = parser.parseCommand(inputData.nextLine());
                    System.out.println(msg.getType());

                    if (msg.getType().equals("server_message")) {
                        ServerMessage typedMessage = (ServerMessage)msg;
                        chat.append(typedMessage.getName() + ":" + "\n" + typedMessage.getContent());
                        chat.append("\n");
                    }

                    if (msg.getType().equals("userlogin")) {
                        ServerUserLoginMessage typedMessage = (ServerUserLoginMessage)msg;
                        usersList.add(typedMessage.getUserName());
                        updateUserList(users);
                    }

                    if (msg.getType().equals("userlogout")) {
                        ServerUserLogoutMessage typedMessage = (ServerUserLogoutMessage)msg;
                        usersList.remove(typedMessage.getUserName());
                        updateUserList(users);
                    }
                }
            }
        });

        messageHandler.start();
    }

    synchronized private LinkedList<String> getUsersList() {
        sendMessage(constructor.createClientListMessage(sessionID));

        while (true) {
            if (inputData.hasNext()) {
                Message msg = parser.parseCommand(inputData.nextLine());

                if (msg.getType().equals("list")) {
                    ServerListMessage typedMessage = (ServerListMessage)msg;
                    return typedMessage.getUsers();
                }
            }
        }
    }

    synchronized private void updateUserList(JTextArea users) {
        users.selectAll();
        users.replaceSelection("");

        users.append("Users online: ");
        users.append("\n");

        for (String user : usersList) {
            users.append(user);
            users.append("\n");
        }
    }

    public void sendMessage(String msg) {
        try {
            outputData.println(msg);
            outputData.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public xmlParser getParser() {
        return parser;
    }

    public xmlMessageConstructor getConstructor() {
        return constructor;
    }

    public Scanner getInputData() {
        return inputData;
    }
}
