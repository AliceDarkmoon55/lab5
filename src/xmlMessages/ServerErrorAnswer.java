package xmlMessages;

public class ServerErrorAnswer extends Message {
    private String reason;

    public ServerErrorAnswer(String reason) {
        super("error");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
