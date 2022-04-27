import java.io.*;
import java.net.*;

public class MyClient_fc {
    public static void main(String[] args) {  
         String username = System.getProperty("user.name");
        try{  
            Socket s=new Socket("localhost",50000);  
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());  //sends a message 

            jobschd(dis, dout, "HELO\n");
            jobschd(dis, dout, "AUTH "+username+"\n");
            String job = jobschd(dis, dout, "REDY\n");
            String[] jobDetails = getJobDetails(job);
            while(job.startsWith("JOBN")) {
                // jobDetails = getJobDetails(job);
                jobschd(dis, dout, "GETS Capable "+jobDetails[1]+" "+jobDetails[2]+" "+jobDetails[3]+"\n");
                job = jobschd(dis, dout, "OK\n");
                String[] capableServer = getCapableServer(job);
                while(!job.equals(".")) {
                    job = jobschd(dis, dout, "OK\n");
                }   
                jobschd(dis, dout, "SCHD "+jobDetails[0]+" "+capableServer[0]+" "+capableServer[1]+"\n");
                job = jobschd(dis, dout, "REDY\n");
                if(job.equals("NONE")){
                    jobschd(dis, dout, "QUIT\n");
                }
                if(job.startsWith("JCPL")) {
                    job = jobschd(dis, dout, "REDY\n");
                }
            }
            dout.close();
            s.close(); 
        }
        catch(Exception e){System.out.println(e);}  
    }

    /**
     * 
     * @param server
     * @return a string array in format {serverName, serverID}
     */
    private static String[] getCapableServer(String server) {
        String[] capableServer = new String[2];

        String serverName = server.substring(0, server.indexOf(' '));
        capableServer[0] = serverName;

        String serverID = server.substring(server.indexOf(' ')+1);
        capableServer[1] = serverID.substring(0, serverID.indexOf(' '));

        return capableServer;
    }

    /**
     * 
     * @param job
     * @return an array in format {jobID, cores, memmory, disk}
     */
    private static String[] getJobDetails(String job) {
        String[] jobDetails = new String[4];

        //jobID
        String temp = job.substring(job.indexOf(' ')+1);
        temp = temp.substring(temp.indexOf(' ')+1);
        String jobID = temp.substring(0, temp.indexOf(' '));
        jobDetails[0] = jobID;

        //cores
        temp = temp.substring(temp.indexOf(' ')+1);
        temp = temp.substring(temp.indexOf(' ')+1);
        String cores = temp.substring(0, temp.indexOf(' '));
        jobDetails[1] = cores;

        //memory
        temp = temp.substring(temp.indexOf(' ')+1);
        String memory = temp.substring(0, temp.indexOf(' '));
        jobDetails[2] = memory;

        //disk
        String disk = temp.substring(temp.indexOf(' ')+1);
        jobDetails[3] = disk;

        return jobDetails;
    }

    public static String jobschd(BufferedReader dis, DataOutputStream dout, String message) throws UnknownHostException, IOException {
        dout.write((message).getBytes());  
        dout.flush();
        String str=(String)dis.readLine();
        System.out.println(str);
        return str;
    }  
}