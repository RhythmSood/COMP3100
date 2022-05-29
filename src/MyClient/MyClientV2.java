import java.io.*;
import java.net.*;
import DataExtracter.jobinfo;
import DataExtracter.serverdata;

public class MyClientV2 {

    private static jobinfo currentJob; // stores info of the current job

    private static final int MAX_RUNTIME = 1000;

    public static void main(String[] args) {
        try {
            // open the socket used to connect to the server
            Socket socket = new Socket("localhost", 50000);

            // initialise for input and output
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //send HELO to server
            sendMessage("HELO", out);

            //recieves OK from server
            receiveMessage(in);

            //send AUTH and authentication info to server
            sendMessage("AUTH" + " " + System.getProperty("user.name"), out);

            //recieves OK from server
            receiveMessage(in);

            //send REDY when ready to start reading jobs
            sendMessage("REDY", out);

            //read first job info
            String msg = receiveMessage(in);
            currentJob = extractJobInfo(msg);

            //schedule first job on the most capable server
            scheduleJobCustom(in, out);

            //recieves OK from server
            receiveMessage(in);

            //used to check whether the reply after REDY is NONE or JOBN
            String command;

            //ends the while loop after all jobs are scheduled
            Boolean moreJobs = true;

            //handles all the jobs
            while (moreJobs) {
                //send REDY for next info
                sendMessage("REDY", out);

                //revieves reply from server
                msg = receiveMessage(in);

                // set command to the first four letters of the reply (NONE, JOBN or JCPL)
                command = msg.substring(0, 4);

                switch (command) {
                    case "JOBN":
                        //extract the job info
                        currentJob = extractJobInfo(msg);

                        //schedule the job
                        scheduleJobCustom(in, out);

                        receiveMessage(in);
                        break;
                    case "NONE":
                        //all jobs have been scheduled
                        moreJobs = false;
                        break;
                    default:
                        break;
                }
            }

            //quit
            sendMessage("QUIT", out);

            receiveMessage(in);

            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // schedules jobs with the fastest and most reliable algo
    private static void scheduleJobCustom(BufferedReader in, DataOutputStream out) {
        try {
            String rply;

            // returns available servers with necessary resources
            serverdata[] availServers = getServersData(in, out, "available");

            //schedule the job to the first available server
            if (availServers != null) {
                //schedule the job
                sendMessage("SCHD " + currentJob.id + " " + availServers[0].type + " " + availServers[0].id, out);
            } else {//if no available server is found, recieve all the capable servers
                serverdata[] capServers = getServersData(in, out, "capable");

                //first server with an estimated runtime under the threshold
                int index = 0;
                int currentEstRuntime = 0;
                do {
                    //retrieve total estimated runtime of server
                    sendMessage("EJWT " + capServers[index].type + " " + capServers[index].id, out);

                    rply = receiveMessage(in);

                    currentEstRuntime = Integer.valueOf(rply);

                    index++;
                } while (currentEstRuntime > MAX_RUNTIME && index < capServers.length);

                index--;

                //schedule the job
                sendMessage("SCHD " + currentJob.id + " " + capServers[index].type + " " + capServers[index].id, out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //sends a message to the server
    private static void sendMessage(String msg, DataOutputStream out) {
        try {
            out.write((msg + "\n").getBytes());
            out.flush();
            // output to console
            System.out.println("Sent: " + msg);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //recieve the message from the server and stores it globally
    private static String receiveMessage(BufferedReader in) {
        try {
            String reply = in.readLine();
            return reply;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    //extracts job info
    private static jobinfo extractJobInfo(String msg) {
        String[] info = msg.split(" ");
        jobinfo job = new jobinfo(info[1], info[2], info[3], info[4], info[5], info[6]);
        return job;
    }

    // extracts server data
    private static serverdata extractServerInfo(String msg) {
        String[] info = msg.split(" ");
        serverdata server = new serverdata(info[0], info[1], info[4], info[5], info[6]);
        return server;
    }

    /**
     * 
     * @param in
     * @param out
     * @param request
     * @return all the servers based on the request
     */
    private static serverdata[] getServersData(BufferedReader in, DataOutputStream out, String request) {
        String msg;
        String rply;

        // send the appropriate request based on the mode
        switch (request) {
            case "all":
                sendMessage("GETS All", out);
                break;
            case "available":
                sendMessage("GETS Avail " + currentJob.reqCores + " " + currentJob.reqMem + " "
                        + currentJob.reqDisk, out);
                break;
            case "capable":
                sendMessage("GETS Capable " + currentJob.reqCores + " " + currentJob.reqMem + " "
                        + currentJob.reqDisk, out);
                break;
            default:
                return null;
        }

        // get data
        rply = receiveMessage(in);

        // send OK
        sendMessage("OK", out);

        // check that there is actually servers to return
        int numServers = Integer.valueOf(rply.split(" ")[1]);

        // return if there are no servers matching request
        if (numServers == 0) {
            receiveMessage(in);
            return null;
        }

        // intialise server array
        serverdata[] servers = new serverdata[numServers];

        // get server info
        for (int i = 0; i < servers.length; i++) {
            msg = receiveMessage(in);
            servers[i] = extractServerInfo(msg);
        }

        // send OK
        sendMessage("OK", out);

        // get and discard reply
        receiveMessage(in);

        return servers;
    }
}