package example;

import com.google.protobuf.AbstractMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtobufMessageUtil {

    private static final Map<String,Class<?>> protobufClassList = new HashMap<>();
    /* Convert Agent Message to Protobuf
        Agent message is base64 encoded.
        The base64 encoded string is the content of an XML node
        The XML provides the name of the message
    */

    public AbstractMessage decode(String data) {
        AbstractMessage msg = null;
        try {
            msg = new ProtobufMessageUtil().decodeXML(data);
        } catch (Exception ex) {
            // logger.error(ex.getMessage());
        }
        return msg;
    }

    private static String xmlHeaderPart1 = "<pb type='";
    private static String xmlHeaderPart2 = "'>";
    private static String xmlFooter = "</pb>";


    private static String encode(AbstractMessage msg, String messageName) {
        StringBuilder encodedMessage = new StringBuilder();
        String msg64 = Base64.getEncoder().encodeToString(msg.toByteArray());
        encodedMessage.append(xmlHeaderPart1).append(messageName).append(xmlHeaderPart2).append(msg64).append(xmlFooter);

        return encodedMessage.toString();
    }

    public AbstractMessage getMessageByRegex(String xmlMsg) {
        String content = MessageRegEx.extractContent(xmlMsg);
        String type = MessageRegEx.extractType(xmlMsg);
        return decodeMessage(type, content);
    }

    public AbstractMessage decodeMessage(String type, String message) {
        byte[] byte_data = Base64.getDecoder().decode(message);

        AbstractMessage abstractMessage = null;
        try {
            abstractMessage = getProtobufByNameFromBytes(type, byte_data);
        } catch (Exception ex) {
            //logger.error("Failed to parse message: " + message, ex);
        }
        return abstractMessage;
    }

    private AbstractMessage decodeXML(String xmlMsg) throws Exception {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // logger.error(e.getMessage());
        }
        Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xmlMsg.getBytes("utf-8"))));

        NodeList nodes = doc.getElementsByTagName("pb");
        Node node = nodes.item(0);

        String messageType = node.getAttributes().getNamedItem("type").getTextContent();
        String base64_msg = node.getTextContent();

        return decodeMessage(messageType, base64_msg);
    }


    /* Create a protobuf object with the given name from the given bytes */
    private AbstractMessage getProtobufByNameFromBytes(String messageName, byte[] data) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String className = "com.ziften.server.protocol.message." + messageName + "Message$" + messageName;

        Class<?> msgClass = null;
        if(protobufClassList.containsKey(className)) {
            msgClass = protobufClassList.get(className);
        }
        else {
            msgClass = Class.forName(className);
            protobufClassList.put(className,msgClass);
        }

        Method parseFrom = msgClass.getDeclaredMethod("parseFrom", byte[].class);
        return (AbstractMessage) parseFrom.invoke(null, data);
    }

}