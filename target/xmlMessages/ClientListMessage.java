package xmlMessages;

public class ClientListMessage extends Message {
    private String sessionId;

    public ClientListMessage(String sesionId) {
        super("getlist");
        this.sessionId = sesionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
