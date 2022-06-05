package xmlMessages;

public class ServerMessage extends Message {
    private String content;
    private String name;

    public ServerMessage(String content, String name) {
        super("server_message");
        this.content = content;
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }
}
