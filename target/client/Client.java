package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import xmlHandlers.XMLConstructor;
import xmlHandlers.XMLParser;
import xmlMessages.Message;
import xmlMessages.ServerListMessage;
import xmlMessages.ServerMessage;
import xmlMessages.ServerNewUserMessage;
import xmlMessages.ServerUserLogoutMessage;

public class Client extends JFrame {
    private XMLParser parser = new XMLParser();
    private XMLConstructor constructor = new XMLConstructor();
    private LinkedList<String> usersList;
    private static final String hostName = "localhost";
    private static final int hostPort = 3000;
    private String sessionID;
    private String userName;
    private Socket clientSocket;
    private Scanner inputData;
    private PrintWriter outputData;
    private Thread messageHandler;

    public Client() {
        try {
            this.clientSocket = new Socket("localhost", 3000);
            this.inputData = new Scanner(this.clientSocket.getInputStream());
            this.outputData = new PrintWriter(this.clientSocket.getOutputStream());
            this.usersList = new LinkedList();
        } catch (IOException var10) {
            var10.printStackTrace();
            System.exit(1);
        }

        this.setBounds(400, 200, 800, 500);
        this.setLayout(new BorderLayout());
        JTextArea chat = new JTextArea();
        chat.setLineWrap(true);
        chat.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chat);
        chatScrollPane.setHorizontalScrollBarPolicy(31);
        chatScrollPane.setVerticalScrollBarPolicy(20);
        chatScrollPane.setPreferredSize(new Dimension(600, 400));
        this.add(chatScrollPane, "Center");
        JTextArea users = new JTextArea();
        JScrollPane usersScrollPane = new JScrollPane(users);
        users.setEditable(false);
        usersScrollPane.setPreferredSize(new Dimension(200, 400));
        usersScrollPane.setHorizontalScrollBarPolicy(31);
        usersScrollPane.setVerticalScrollBarPolicy(20);
        this.add(usersScrollPane, "East");
        Container sender = new Container();
        sender.setLayout(new BorderLayout());
        JTextArea textMessage = new JTextArea();
        JScrollPane textMessageScrollPane = new JScrollPane(textMessage);
        textMessageScrollPane.setPreferredSize(new Dimension(600, 50));
        textMessageScrollPane.setHorizontalScrollBarPolicy(31);
        textMessageScrollPane.setVerticalScrollBarPolicy(20);
        sender.add(textMessageScrollPane, "Center");
        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(200, 50));
        sender.add(sendButton, "East");
        this.add(sender, "South");
        new ClientEntryDialog("Login", true, this);
        this.usersList = this.getUsersList();
        this.refreshUserList(users);
        sendButton.addActionListener((e) -> {
            if (!textMessage.getText().trim().isEmpty()) {
                String message = this.constructor.createClientMessage(textMessage.getText(), this.sessionID);
                this.sendMessage(message);
                textMessage.selectAll();
                textMessage.replaceSelection("");
            }

        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Client.this.messageHandler.interrupt();
                Client.this.sendMessage(Client.this.constructor.createUserLogoutMessage(Client.this.sessionID));
                Client.this.inputData.close();
                Client.this.outputData.close();
                System.exit(1);
            }
        });
        this.startWorking(chat, users);
        this.setVisible(true);
    }

    private void startWorking(JTextArea chat, JTextArea users) {
        this.messageHandler = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                if (this.inputData.hasNext()) {
                    Message message = this.parser.parseCommand(this.inputData.nextLine());
                    System.out.println(message.getType());
                    if (message.getType().equals("server_message")) {
                        ServerMessage typedMessagex = (ServerMessage)message;
                        chat.append(typedMessagex.getName() + ":\n" + typedMessagex.getContent());
                        chat.append("\n");
                    }

                    if (message.getType().equals("userlogin")) {
                        ServerNewUserMessage typedMessagexx = (ServerNewUserMessage)message;
                        this.usersList.add(typedMessagexx.getUserName());
                        this.refreshUserList(users);
                    }

                    if (message.getType().equals("userlogout")) {
                        ServerUserLogoutMessage typedMessage = (ServerUserLogoutMessage)message;
                        this.usersList.remove(typedMessage.getUserName());
                        this.refreshUserList(users);
                    }
                }
            }

        });
        this.messageHandler.start();
    }

    private synchronized LinkedList<String> getUsersList() {
        this.sendMessage(this.constructor.createGetUsersMessage(this.sessionID));

        Message message;
        do {
            while(!this.inputData.hasNext()) {
            }

            message = this.parser.parseCommand(this.inputData.nextLine());
        } while(!message.getType().equals("list"));

        ServerListMessage typedMessage = (ServerListMessage)message;
        return typedMessage.getUsers();
    }

    private synchronized void refreshUserList(JTextArea users) {
        users.selectAll();
        users.replaceSelection("");
        users.append("Users online: ");
        users.append("\n");
        Iterator var2 = this.usersList.iterator();

        while(var2.hasNext()) {
            String user = (String)var2.next();
            users.append(user);
            users.append("\n");
        }

    }

    public void sendMessage(String msg) {
        try {
            this.outputData.println(msg);
            this.outputData.flush();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public XMLParser getParser() {
        return this.parser;
    }

    public XMLConstructor getConstructor() {
        return this.constructor;
    }

    public String getUserName() {
        return this.userName;
    }

    public Scanner getInputData() {
        return this.inputData;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
