package xmlMessages;

public class ClientListMessage extends Message {
    private String sessionID;

    public ClientListMessage(String sessionID) {
        super("client_list");
        this.sessionID = sessionID;
    }

    public String getSessionId() {
        return sessionID;
    }
}
