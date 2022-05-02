import java.io.*;
import java.net.*;
import java.util.List;

public class MyClientFC {
    static List<List<String>> servers;
    public static void main(String[] args) {
         String username = System.getProperty("user.name");
        try{  
            Socket s=new Socket("localhost",50000);  
            BufferedReader dis=new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());  //sends a message 

            jobschd(dis, dout, "HELO\n");
            jobschd(dis, dout, "AUTH "+username+"\n");
            xmlParserFC.main(null);
            servers = xmlParserFC.server;
            String job = jobschd(dis, dout, "REDY\n");
            while(!job.equals("NONE")) {
                if(job.startsWith("JCPL")) {
                    job = jobschd(dis, dout, "REDY\n");
                } else if(job.startsWith("JOBN")){
                    jobschd(dis, dout, "SCHD "+ getJobID(job)+" "+ bestCapableServer(job)+" "+"0\n");
                    job = jobschd(dis, dout, "REDY\n");
                } else {
                    job = jobschd(dis, dout, "REDY\n");
                }                    
            }
            jobschd(dis, dout, "QUIT\n");
            dout.close();
            s.close(); 
        }
        catch(Exception e){System.out.println(e);}  
    }

    public static String bestCapableServer(String job) {
        String capableServer = "";
        String[] jobDetails = getJobDetails(job);
        int cores = Integer.parseInt(jobDetails[0]);
        int memory = Integer.parseInt(jobDetails[1]);
        int disk = Integer.parseInt(jobDetails[2]);

        for(List<String> x : servers) {
            if(Integer.parseInt(x.get(1)) >= cores && Integer.parseInt(x.get(2)) >= memory && Integer.parseInt(x.get(3)) >= disk) {
                capableServer = x.get(0);
                break;
            }
        }
        return capableServer;
    }  

    private static String[] getJobDetails(String job) {
        String[] jobDetails = new String[3];

        //jobID
        String temp = job.substring(job.indexOf(' ')+1);
        temp = temp.substring(temp.indexOf(' ')+1);

        //cores
        temp = temp.substring(temp.indexOf(' ')+1);
        temp = temp.substring(temp.indexOf(' ')+1);
        String cores = temp.substring(0, temp.indexOf(' '));
        jobDetails[0] = cores;

        //memory
        temp = temp.substring(temp.indexOf(' ')+1);
        String memory = temp.substring(0, temp.indexOf(' '));
        jobDetails[1] = memory;

        //disk
        String disk = temp.substring(temp.indexOf(' ')+1);
        jobDetails[2] = disk;

        return jobDetails;
    }

    public static String getJobID(String job) {
        String temp = job.substring(job.indexOf(' ')+1);
        temp = temp.substring(temp.indexOf(' ')+1);
        String jobID = temp.substring(0, temp.indexOf(' '));

        return jobID;
    }

    public static String jobschd(BufferedReader dis, DataOutputStream dout, String message) throws UnknownHostException, IOException {
        dout.write((message).getBytes());  
        dout.flush();
        String  str=(String)dis.readLine();
        System.out.println(str);
        return str;
    }  
}
