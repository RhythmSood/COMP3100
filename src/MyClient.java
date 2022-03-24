import java.io.*;  
import java.net.*;

public class MyClient {  
    public static void main(String[] args) {  
        try{      
            Socket s=new Socket("localhost",50000);  
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());  //sends a message
            // String username = System.getProperty("Rhythm");
            // sending 1st message
            dout.write(("HELO\n").getBytes());  
            dout.flush();  
            // reading 1st server message
            String  str=(String)dis.readLine();  
            System.out.println("message= "+str);
            // dout.write(("AUTH Rhythm\n").getBytes());  
            // dout.flush();
            // // sending 2nd message
            // dout.write(("REDY\n").getBytes());
            // dout.flush(); 
            // //reading 2nd message
            // String  str3=(String)dis.readLine();  
            // System.out.println("message= "+str3); 
            dout.close();
            s.close();  
        }
        catch(Exception e){System.out.println(e);}  
    }  
}  