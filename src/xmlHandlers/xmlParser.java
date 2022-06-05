package xmlHandlers;

import xmlMessages.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class xmlParser {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public Message parseCommand(String xml) {
        Message msg = null;

        try {
            Document xmlInputMessage = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            String tag = xmlInputMessage.getDocumentElement().getTagName();

            if (tag.equals("message")) {

                String type = xmlInputMessage.getDocumentElement().getFirstChild().getTextContent();
                NodeList nodeList;
                String content;

                switch (type) {
                    case "client_list":
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        msg = new ClientListMessage(nodeList.item(0).getTextContent());
                        break;

                    case "login":
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        msg = new ClientLoginMessage(nodeList.item(0).getTextContent());
                        break;

                    case "logout":
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        msg = new ClientLogoutMessage(nodeList.item(0).getTextContent());
                        break;

                    case "client_message":
                        nodeList = xmlInputMessage.getElementsByTagName("content");
                        content = nodeList.item(0).getTextContent().replaceAll("\\\\n", "\\\n");
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        msg = new ClientMessage(content, nodeList.item(0).getTextContent());
                        break;

                    case "refuse":
                        msg = new ClientRefuseMessage();
                        break;

                    case "error":
                        nodeList = xmlInputMessage.getElementsByTagName("reason");
                        msg = new ServerErrorAnswer(nodeList.item(0).getTextContent());
                        break;

                    case "list":
                        LinkedList<String> users = new LinkedList<>();
                        nodeList = xmlInputMessage.getElementsByTagName("user");

                        for (int i = 0; i < nodeList.getLength(); i++) {
                            users.add(nodeList.item(i).getTextContent());
                        }
                        msg = new ServerListMessage(users);
                        break;

                    case "server_message":
                        nodeList = xmlInputMessage.getElementsByTagName("content");
                        content = nodeList.item(0).getTextContent().replaceAll("\\\\n", "\\\n");
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        msg = new ServerMessage(content, nodeList.item(0).getTextContent());
                        break;

                    case "success":
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        msg = new ServerSuccessAnswer(nodeList.item(0).getTextContent());
                        break;

                    case "userlogin":
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        msg = new ServerUserLoginMessage(nodeList.item(0).getTextContent());
                        break;

                    case "userlogout":
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        msg = new ServerUserLogoutMessage(nodeList.item(0).getTextContent());
                        break;
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error: " + e);
        }

        return msg;
    }
}
