package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import xmlHandlers.XMLConstructor;
import xmlHandlers.XMLParser;
import xmlMessages.ClientListMessage;
import xmlMessages.ClientLoginMessage;
import xmlMessages.ClientLogoutMessage;
import xmlMessages.ClientMessage;
import xmlMessages.Message;

public class ClientHandler implements Runnable {
    private XMLParser parser = new XMLParser();
    private XMLConstructor constructor = new XMLConstructor();
    private static LinkedList<String> names = new LinkedList();
    private final String uniqueSessionID = String.valueOf((new Random()).nextInt());
    private String curName;
    private Scanner inputData;
    private PrintWriter outputData;
    private Server server;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            this.server = server;
            this.inputData = new Scanner(clientSocket.getInputStream());
            this.outputData = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void run() {
        try {
            this.setConnection();

            while(!Thread.currentThread().isInterrupted()) {
                if (this.inputData.hasNext()) {
                    Message message = this.parser.parseCommand(this.inputData.nextLine());
                    System.out.println(message.getType());
                    if (message.getType().equals("client_message")) {
                        ClientMessage typedMessage = (ClientMessage)message;
                        if (this.uniqueSessionID.equals(typedMessage.getSessionId())) {
                            this.server.sendMessageToAllClients(this.constructor.createServerMessage(typedMessage.getContent(), this.curName));
                        }
                    }

                    if (message.getType().equals("logout")) {
                        ClientLogoutMessage typedMessage = (ClientLogoutMessage)message;
                        if (this.uniqueSessionID.equals(typedMessage.getSessionId())) {
                            synchronized(names) {
                                names.remove(this.curName);
                            }

                            this.server.removeClientHandler(this);
                            Thread.currentThread().interrupt();
                            this.server.sendMessageToAllClients(this.constructor.createUserExitMessage(this.curName));
                            this.inputData.close();
                            this.outputData.close();
                            this.server.getClientSocket().close();
                        }
                    }

                    if (message.getType().equals("getlist")) {
                        ClientListMessage typedMessage = (ClientListMessage)message;
                        if (this.uniqueSessionID.equals(typedMessage.getSessionId())) {
                            this.sendMessage(this.constructor.createUsersListMessage(names));
                        }
                    }
                }
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    private void setConnection() throws IOException {
        while(true) {
            if (!Thread.currentThread().isInterrupted()) {
                if (!this.inputData.hasNext()) {
                    continue;
                }

                Message message = this.parser.parseCommand(this.inputData.nextLine());
                System.out.println(message.getType());
                if (!message.getType().equals("login")) {
                    if (message.getType().equals("refuse")) {
                        this.inputData.close();
                        this.outputData.close();
                        this.server.getClientSocket().close();
                        this.server.getClientHandlers().remove(this);
                        Thread.currentThread().interrupt();
                        continue;
                    }

                    this.sendMessage(this.constructor.createErrorMessage("Please repeat again"));
                    continue;
                }

                ClientLoginMessage typedMessage = (ClientLoginMessage)message;
                this.curName = typedMessage.getUserName();
                if (names.contains(this.curName)) {
                    System.out.println(this.curName);
                    this.sendMessage(this.constructor.createErrorMessage("User already exists"));
                    continue;
                }

                synchronized(names) {
                    names.add(this.curName);
                }

                this.sendMessage(this.constructor.createConfirmMessage(this.uniqueSessionID));
                this.server.sendMessageToAllClients(this.constructor.createNewUserMessage(this.curName));
            }

            return;
        }
    }

    public synchronized void sendMessage(String msg) {
        try {
            this.outputData.println(msg);
            this.outputData.flush();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
