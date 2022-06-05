package xmlHandlers;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import xmlMessages.ClientListMessage;
import xmlMessages.ClientLoginMessage;
import xmlMessages.ClientLogoutMessage;
import xmlMessages.ClientMessage;
import xmlMessages.ClientRefuseMessage;
import xmlMessages.ErrorMessage;
import xmlMessages.Message;
import xmlMessages.ServerConfirmMessage;
import xmlMessages.ServerListMessage;
import xmlMessages.ServerMessage;
import xmlMessages.ServerNewUserMessage;
import xmlMessages.ServerUserLogoutMessage;
import xmlMessages.SuccessMessage;

public class XMLParser {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public XMLParser() {
    }

    public Message parseCommand(String xml) {
        Object message = null;

        try {
            Document xmlInputMessage = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            String tag = xmlInputMessage.getDocumentElement().getTagName();
            if (tag.equals("message")) {
                String type = xmlInputMessage.getDocumentElement().getFirstChild().getTextContent();
                byte var9 = -1;
                switch(type.hashCode()) {
                    case -1992049387:
                        if (type.equals("userlogout")) {
                            var9 = 10;
                        }
                        break;
                    case -1867169789:
                        if (type.equals("success")) {
                            var9 = 1;
                        }
                        break;
                    case -1097329270:
                        if (type.equals("logout")) {
                            var9 = 6;
                        }
                        break;
                    case -934813676:
                        if (type.equals("refuse")) {
                            var9 = 11;
                        }
                        break;
                    case -902914157:
                        if (type.equals("client_message")) {
                            var9 = 7;
                        }
                        break;
                    case -876158197:
                        if (type.equals("server_message")) {
                            var9 = 8;
                        }
                        break;
                    case -775651618:
                        if (type.equals("connection")) {
                            var9 = 3;
                        }
                        break;
                    case -74406668:
                        if (type.equals("getlist")) {
                            var9 = 4;
                        }
                        break;
                    case 3322014:
                        if (type.equals("list")) {
                            var9 = 5;
                        }
                        break;
                    case 96784904:
                        if (type.equals("error")) {
                            var9 = 0;
                        }
                        break;
                    case 103149417:
                        if (type.equals("login")) {
                            var9 = 2;
                        }
                        break;
                    case 351382142:
                        if (type.equals("userlogin")) {
                            var9 = 9;
                        }
                }

                NodeList nodeList;
                String content;
                switch(var9) {
                    case 0:
                        nodeList = xmlInputMessage.getElementsByTagName("reason");
                        message = new ErrorMessage(nodeList.item(0).getTextContent());
                        break;
                    case 1:
                        message = new SuccessMessage();
                        break;
                    case 2:
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        message = new ClientLoginMessage(nodeList.item(0).getTextContent());
                        break;
                    case 3:
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        message = new ServerConfirmMessage(nodeList.item(0).getTextContent());
                        break;
                    case 4:
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        message = new ClientListMessage(nodeList.item(0).getTextContent());
                        break;
                    case 5:
                        LinkedList<String> users = new LinkedList();
                        nodeList = xmlInputMessage.getElementsByTagName("user");

                        for(int i = 0; i < nodeList.getLength(); ++i) {
                            users.add(nodeList.item(i).getTextContent());
                        }

                        message = new ServerListMessage(users);
                        break;
                    case 6:
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        message = new ClientLogoutMessage(nodeList.item(0).getTextContent());
                        break;
                    case 7:
                        nodeList = xmlInputMessage.getElementsByTagName("content");
                        content = nodeList.item(0).getTextContent().replaceAll("\\\\n", "\\\n");
                        nodeList = xmlInputMessage.getElementsByTagName("session");
                        message = new ClientMessage(content, nodeList.item(0).getTextContent());
                        break;
                    case 8:
                        nodeList = xmlInputMessage.getElementsByTagName("content");
                        content = nodeList.item(0).getTextContent().replaceAll("\\\\n", "\\\n");
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        message = new ServerMessage(content, nodeList.item(0).getTextContent());
                        break;
                    case 9:
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        message = new ServerNewUserMessage(nodeList.item(0).getTextContent());
                        break;
                    case 10:
                        nodeList = xmlInputMessage.getElementsByTagName("name");
                        message = new ServerUserLogoutMessage(nodeList.item(0).getTextContent());
                        break;
                    case 11:
                        nodeList = xmlInputMessage.getElementsByTagName("refuse");
                        message = new ClientRefuseMessage();
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException var12) {
            var12.printStackTrace();
        }

        return (Message)message;
    }
}
