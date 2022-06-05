package xmlMessages;

public class ClientMessage extends Message {
    private String content;
    private String sessionID;

    public ClientMessage(String content, String sessionId) {
        super("client_message");
        this.content = content;
        this.sessionID = sessionId;
    }

    public String getContent() {
        return content;
    }

    public String getSessionID() {
        return sessionID;
    }
}
