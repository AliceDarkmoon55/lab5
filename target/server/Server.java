package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
    private Socket clientSocket;
    private ArrayList<ClientHandler> clientHandlers = new ArrayList();

    public ArrayList<ClientHandler> getClientHandlers() {
        return this.clientHandlers;
    }

    public Server() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);

        while(true) {
            this.clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(this.clientSocket, this);
            (new Thread(clientHandler)).start();
            this.clientHandlers.add(clientHandler);
        }
    }

    public void sendMessageToAllClients(String msg) {
        Iterator var2 = this.clientHandlers.iterator();

        while(var2.hasNext()) {
            ClientHandler clientHandler = (ClientHandler)var2.next();
            clientHandler.sendMessage(msg);
        }

    }

    synchronized void removeClientHandler(ClientHandler clientHandler) {
        this.clientHandlers.remove(clientHandler);
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }
}
