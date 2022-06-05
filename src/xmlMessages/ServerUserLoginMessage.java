package xmlMessages;

public class ServerUserLoginMessage extends Message {
    private String userName;

    public ServerUserLoginMessage(String userName){
        super("userlogin");
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
