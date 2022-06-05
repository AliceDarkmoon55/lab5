package xmlMessages;

public class ClientLogoutMessage extends Message {
    private String sessionID;

    public ClientLogoutMessage(String sessionID) {
        super("logout");
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }
}
