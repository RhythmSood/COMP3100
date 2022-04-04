// package XMLparser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
// import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class xmlParser extends DefaultHandler{
    private StringBuilder currentValue = new StringBuilder();

  List<List<String>> server = new ArrayList<List<String>>();
  List<String> largestServer = new ArrayList<>();

  
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
          int maxCores = 0;
          for(List<String> x : server) {
              
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


  public void characters(char ch[], int start, int length) {
      currentValue.append(ch, start, length);

  }
}
