import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class xmlReader {
    public static final String FILENAME = "./ds-system.xml";
    private static List<String> server = new ArrayList<>();

    public static void main(String[] args) {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {

            SAXParser saxParser = factory.newSAXParser();

            xmlParser handler = new xmlParser();
            saxParser.parse(FILENAME, handler);
            server = handler.largestServer();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> bigServer() {
        main(null);
        return server;
    }
}
