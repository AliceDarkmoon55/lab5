package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args)  throws IOException {
        Server server = new Server();
    }

    private Socket clientSocket;
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public Server() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8189);
        while (true) {
            clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            new Thread(clientHandler).start();
            clientHandlers.add(clientHandler);
        }
    }

    public void sendMessageToAllClients(String msg) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(msg);
        }
    }

    synchronized void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}
