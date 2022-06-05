package xmlMessages;

public class ServerNewUserMessage extends Message {
    private String userName;

    public ServerNewUserMessage(String userName) {
        super("userlogin");
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
