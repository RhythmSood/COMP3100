import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class xmlParserFC extends DefaultHandler {
    public static final String FILENAME = "./ds-system.xml";
    private StringBuilder currentValue = new StringBuilder();

    static List<List<String>> server = new ArrayList<List<String>>();

    public static void main(String[] args) {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            SAXParser saxParser = factory.newSAXParser();

            xmlParserFC handler = new xmlParserFC();
            saxParser.parse(FILENAME, handler);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
  
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) {

        // reset the tag value
        currentValue.setLength(0);

        List<String> temp = new ArrayList<String>();

        //   System.out.printf("Start Element : %s%n", qName);

        if (qName.equalsIgnoreCase("server")) {
            // get tag's attribute by name
            String type = attributes.getValue("type");
            String cores = attributes.getValue("cores");
            String memory = attributes.getValue("memory");
            String disk = attributes.getValue("disk");
            temp.add(type);
            temp.add(cores);
            temp.add(memory);
            temp.add(disk);
            server.add(temp);
            // System.out.println("Server type:"+" "+type+" ,cores:"+cores+" ,memory:"+memory);
        }
    }

    public void characters(char ch[], int start, int length) {
        currentValue.append(ch, start, length);

    }
}
