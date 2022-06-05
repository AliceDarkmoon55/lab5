package xmlMessages;

public class ServerConfirmMessage extends Message {
    private String sessionId;

    public ServerConfirmMessage(String sessionId) {
        super("connection");
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
