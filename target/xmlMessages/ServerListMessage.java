package xmlMessages;

import java.util.LinkedList;

public class ServerListMessage extends Message {
    private LinkedList<String> users;

    public ServerListMessage(LinkedList<String> users) {
        super("list");
        this.users = users;
    }

    public LinkedList<String> getUsers() {
        return this.users;
    }
}
