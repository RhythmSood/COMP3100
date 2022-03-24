import java.io.*;  
import java.net.*;

public class MyClient {  
    public static void main(String[] args) {  
        try{  
            Socket s=new Socket("localhost",50000);  
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());  //sends a message 

            jobschd(dis, dout, "HELO\n", 1);
            jobschd(dis, dout, "AUTH Rhythm\n", 1);
            jobschd(dis, dout, "REDY\n", 1);
            String nRecs = jobschd(dis, dout, "GETS All\n", 1);
            jobschd(dis, dout, "OK\n", Integer.parseInt(nRecs));

            dout.close();
            s.close(); 
        }
        catch(Exception e){System.out.println(e);}  
    }

    public static String jobschd(BufferedReader dis, DataOutputStream dout, String message, int repeat) throws UnknownHostException, IOException {
        int i = 1;
        String nRecs = "";
        while(i <= repeat) {
            dout.write((message).getBytes());  
            dout.flush();
            String  str=(String)dis.readLine();
            if(str.startsWith("DATA")) {
                nRecs = str.substring(str.indexOf(' ')+1, str.lastIndexOf(' '));  
                return nRecs;
            }
            System.out.println(str);
            i++;
        }
        return "";
    }  
}  