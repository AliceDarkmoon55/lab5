package server;

import xmlHandlers.xmlMessageConstructor;
import xmlHandlers.xmlParser;
import xmlMessages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private xmlParser parser = new xmlParser();
    private xmlMessageConstructor constructor = new xmlMessageConstructor();

    private Socket clientSocket;
    private Server server;
    private Scanner inputMsg;
    private PrintWriter outputMsg;
    private static LinkedList<String> names = new LinkedList<>();
    private String sessionID = String.valueOf(new Random().nextInt());
    private String userName;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            this.clientSocket = clientSocket;
            this.server = server;
            inputMsg = new Scanner(clientSocket.getInputStream());
            outputMsg = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    @Override
    public void run() {
        try {
            setConnection();

            while (!Thread.currentThread().isInterrupted()) {
                if (inputMsg.hasNext()) {
                    Message msg = parser.parseCommand(inputMsg.nextLine());

                    System.out.println(msg.getType());

                    if (msg.getType().equals("client_message")) {
                        ClientMessage typedMsg = (ClientMessage) msg;
                        if (sessionID.equals(typedMsg.getSessionID())) {
                            server.sendMessageToAllClients(constructor.createServerMessage(typedMsg.getContent(), userName));
                        }
                    }

                    if (msg.getType().equals("logout")) {
                        ClientLogoutMessage typedMsg = (ClientLogoutMessage) msg;
                        if (sessionID.equals(typedMsg.getSessionID())) {

                            synchronized (names) {
                                names.remove(userName);
                            }

                            server.removeClientHandler(this);

                            Thread.currentThread().interrupt();

                            server.sendMessageToAllClients(constructor.createServerUserLogoutMessage(userName));

                            inputMsg.close();
                            outputMsg.close();
                            clientSocket.close();
                        }
                    }

                    if (msg.getType().equals("client_list")) {
                        ClientListMessage typedMsg = (ClientListMessage) msg;
                        if (sessionID.equals(typedMsg.getSessionId())) {
                            sendMessage(constructor.createUsersListMessage(names));
                        }
                    }
                }
            }
        } catch (IOException e){
            System.err.println("Error: " + e);
        }
    }

    private void setConnection() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            if (inputMsg.hasNext()) {
                Message msg = parser.parseCommand(inputMsg.nextLine());

                System.out.println(msg.getType());

                if (msg.getType().equals("login")) {
                    ClientLoginMessage typedMsg = (ClientLoginMessage) msg;
                    userName = typedMsg.getUserName();

                    if (names.contains(userName)) {
                        System.out.println(userName);
                        sendMessage(constructor.createServerErrorAnswer("User" + userName + "already exists"));
                    } else {
                        synchronized (names) {
                            names.add(userName);
                        }

                        sendMessage(constructor.createServerSuccessAnswer(sessionID));
                        server.sendMessageToAllClients(constructor.createServerUserLoginMessage(userName));
                        break;
                    }
                } else if (msg.getType().equals("refuse")) {
                    inputMsg.close();
                    outputMsg.close();
                    clientSocket.close();

                    server.getClientHandlers().remove(this);
                    Thread.currentThread().interrupt();
                } else {
                    sendMessage(constructor.createServerErrorAnswer("Please try again"));
                }
            }
        }
    }

    synchronized public void sendMessage(String msg) {
        outputMsg.println(msg);
        outputMsg.flush();
    }
}
