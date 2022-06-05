package xmlHandlers;

import java.util.LinkedList;

public class xmlMessageConstructor {
    public String createClientListMessage(String sessionID) {
        return "<message>" +
                "<type>client_list</type>" +
                "<session>" + sessionID + "</session>" +
                "</message>";
    }

    public String createClientLoginMessage(String name) {
        return "<message>" +
                "<type>login</type>" +
                "<name>" + name + "</name>" +
                "</message>";
    }

    public String createClientLogoutMessage(String sessionID) {
        return "<message>" +
                "<type>logout</type>" +
                "<session>" + sessionID + "</session>" +
                "</message>";
    }

    public String createClientMessage(String textMessage, String sessionID) {
        return "<message>" +
                "<type>client_message</type>" +
                "<content>" + textMessage + "</content>" +
                "<session>" + sessionID + "</session>" +
                "</message>";
    }

    public String createRefuseMessage() {
        return "<message>" +
                "<type>refuse</type>" +
                "<session>" + "</session>" +
                "</message>";
    }

    public String createServerErrorAnswer(String errorReason) {
        return "<message>" +
                "<type>error</type>" +
                "<reason>" + errorReason + "</reason>" +
                "</message>";
    }

    public String createUsersListMessage(LinkedList<String> users) {
        StringBuilder msg = new StringBuilder("<message>" + "<type>list</type>" + "<listusers>");
        for (String user : users) {
            msg.append("<user>").append(user).append("</user>");
        }
        msg.append("</listusers>" + "</message>");

        return msg.toString();
    }

    public String createServerMessage(String textMessage, String name) {
        return "<message>" +
                "<type>server_message</type>" +
                "<content>" + textMessage + "</content>" +
                "<name>" + name + "</name>" +
                "</message>";
    }

    public String createServerSuccessAnswer(String sessionID) {
        return "<message>" +
                "<type>success</type>" +
                "<session>" + sessionID + "</session>" +
                "</message>";
    }

    public String createServerUserLoginMessage(String name) {
        return "<message>" +
                "<type>userlogin</type>" +
                "<name>" + name + "</name>" +
                "</message>";
    }

    public String createServerUserLogoutMessage(String name) {
        return "<message>" +
                "<type>userlogout</type>" +
                "<name>" + name + "</name>" +
                "</message>";
    }
}
