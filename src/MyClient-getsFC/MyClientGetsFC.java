import java.io.*;
import java.net.*;

public class MyClientGetsFC {
    static String serverType = "";
    static String serverID = "";
    static int totalServers;
    
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost",50000);  
            BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            jobschd(dis, dout, "HELO\n");
            jobschd(dis, dout, "AUTH Rhythm\n");

            while(true) {
                String[] jobn = jobschd(dis, dout, "REDY\n").split(" ");
                if(jobn[0].equals("NONE")) {
                    break;
                }

                if(jobn[0].equals("JOBN")){ 
                    String[] data = jobschd(dis, dout, "GETS Capable "+jobn[4]+" "+jobn[5]+" "+jobn[6]+"\n").split(" ");
                    totalServers = Integer.parseInt(data[1]);

                    dout.write(("OK\n").getBytes());  
                    dout.flush();            

                    for(int i = 0; i < totalServers; i++) {
                        String reply = dis.readLine();
                        if(i == 0) {
                            String[] servers = reply.split(" ");
                            serverType = servers[0];
                            serverID = servers[1];
                        }
                        System.out.println(reply);
                    }

                    jobschd(dis, dout, "OK\n");
                    jobschd(dis, dout, "SCHD "+jobn[2]+" "+serverType+" "+serverID+"\n");
                }
            }
            jobschd(dis, dout, "QUIT\n");
            dis.close();
            dout.close();
            s.close(); 
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static String jobschd(BufferedReader dis, DataOutputStream dout, String message) throws UnknownHostException, IOException {
        dout.write((message).getBytes());  
        dout.flush();
        String str = (String)dis.readLine();
        System.out.println(str);
        return str;
    }  
}
