package xmlMessages;

public class ClientMessage extends Message {
    private String content;
    private String sessionId;

    public ClientMessage(String content, String sessionId) {
        super("client_message");
        this.content = content;
        this.sessionId = sessionId;
    }

    public String getContent() {
        return this.content;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
