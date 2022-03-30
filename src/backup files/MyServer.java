import java.io.*;  
import java.net.*;  
public class MyServer{  
    public static void main(String[] args){  
        try{  
            ServerSocket ss=new ServerSocket(8080);  
            Socket s=ss.accept();//establishes connection   
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream()); //sends a message

            // reading 1st client message
            String  str=(String)dis.readLine();  
            System.out.println("message= "+str);
            // sending 1st message
            dout.write(("OK\n").getBytes());  
            dout.flush();  
            //reading 2nd message
            String  str2=(String)dis.readLine();  
            System.out.println("message= "+str2);
            // sending 2nd message
            dout.write(("OK\n").getBytes());  
            dout.flush();
            dout.write(("JOBN 172 4 320 2 50 120\n").getBytes());  
            dout.flush();  
            dout.close();  //output message stream closed
            ss.close();  //socket connection closed
        }
        catch(Exception e){System.out.println(e);
        }  
    }  
}  