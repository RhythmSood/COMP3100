import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class xmlParser extends DefaultHandler {

  private StringBuilder currentValue = new StringBuilder();

  List<List<String>> server = new ArrayList<List<String>>();
  List<String> largestServer = new ArrayList<>();

  @Override
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
          String limit = attributes.getValue("limit");
          String cores = attributes.getValue("cores");
          String memory = attributes.getValue("memory");
          temp.add(type);
          temp.add(limit);
          temp.add(cores);
          temp.add(memory);
          server.add(temp);
          //System.out.println("Server type:"+" "+type+" ,limit:"+limit+" ,cores:"+cores+" ,memory:"+memory);
          for(List<String> x : server) {
              int maxCores = 0;
              if(Integer.parseInt(x.get(2)) > maxCores) {
                maxCores = Integer.parseInt(x.get(2));
                largestServer = x;
              }
          }
      }
  }

  public List<String> largestServer() {
      return largestServer;
  }

  @Override
  public void endElement(String uri,
                         String localName,
                         String qName) {

    //   System.out.printf("End Element : %s%n", qName);
  }

  // http://www.saxproject.org/apidoc/org/xml/sax/ContentHandler.html#characters%28char%5B%5D,%20int,%20int%29
  // SAX parsers may return all contiguous character data in a single chunk,
  // or they may split it into several chunks
  @Override
  public void characters(char ch[], int start, int length) {

      // The characters() method can be called multiple times for a single text node.
      // Some values may missing if assign to a new string

      // avoid doing this
      // value = new String(ch, start, length);

      // better append it, works for single or multiple calls
      currentValue.append(ch, start, length);

  }

}