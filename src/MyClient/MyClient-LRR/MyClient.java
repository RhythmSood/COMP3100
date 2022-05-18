import java.io.*;
import java.net.*;

public class MyClient {
    public static void main(String[] args) {  
         int count = 0;

         String username = System.getProperty("user.name");
        try{  
            Socket s=new Socket("localhost",50000);  
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());  //sends a message 

            jobschd(dis, dout, "HELO\n");
            jobschd(dis, dout, "AUTH "+username+"\n");
            xmlReader handler = new xmlReader();
            int serverLimit = Integer.parseInt(handler.bigServer().get(1)); //number of servers of one type
            String job = jobschd(dis, dout, "REDY\n");
            while(!job.equals("NONE") && count < serverLimit) {
                if(job.startsWith("JCPL")) {
                    job = jobschd(dis, dout, "REDY\n");
                } else if(job.startsWith("JOBN")){
                    String temp = job.substring(job.indexOf(' ')+1);
                    temp = temp.substring(temp.indexOf(' ')+1);
                    String jobID = temp.substring(0, temp.indexOf(' '));
                    jobschd(dis, dout, "SCHD "+jobID+" "+handler.bigServer().get(0)+" "+count+"\n");
                    count++;
                    job = jobschd(dis, dout, "REDY\n");
                } else
                    job = jobschd(dis, dout, "REDY\n");

                if(count >= serverLimit) {
                    count = 0;
                }
            }
            jobschd(dis, dout, "QUIT\n");
            dout.close();
            s.close(); 
        }
        catch(Exception e){System.out.println(e);}  
    }

    public static String jobschd(BufferedReader dis, DataOutputStream dout, String message) throws UnknownHostException, IOException {
        dout.write((message).getBytes());  
        dout.flush();
        String  str=(String)dis.readLine();
        System.out.println(str);
        return str;
    }  
}