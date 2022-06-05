package xmlMessages;

public class ServerSuccessAnswer extends Message {
    private String sessionID;

    public ServerSuccessAnswer(String sessionID) {
        super("success");
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }
}
