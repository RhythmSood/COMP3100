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

            // send HELO to server
            sendMessage("HELO", out);

            // recieves OK from server
            receiveMessage(in);

            // send AUTH and authentication info to server
            sendMessage("AUTH" + " " + System.getProperty("user.name"), out);

            // recieves OK from server
            receiveMessage(in);

            // send REDY when ready to start reading jobs
            sendMessage("REDY", out);

            // read first job
            String msg = receiveMessage(in);
            currentJob = extractJobInfo(msg);

            // schedule first job
            scheduleJobCustom(in, out);

            // wait for OK
            receiveMessage(in);

            // used for switching based on what the server's reply to REDY is
            String command;

            // used to break out of loop if no more jobs remain
            Boolean moreJobs = true;

            // main loop, handles all messages from the server from now on
            while (moreJobs) {
                // send REDY for next info
                sendMessage("REDY", out);

                // get server's reply
                msg = receiveMessage(in);

                // set command so we can check how to handle reply
                command = msg.substring(0, 4);

                // perform appropriate action based on server reply
                switch (command) {
                    case "JOBN":
                        // schedule the job
                        currentJob = extractJobInfo(msg);

                        scheduleJobCustom(in, out);

                        receiveMessage(in);
                        break;
                    case "NONE":
                        // there are no more jobs so stop the loop
                        moreJobs = false;
                        break;
                    default:
                        break;
                }
            }

            // quit
            sendMessage("QUIT", out);

            receiveMessage(in);

            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // schedules jobs according to a custom algorithm
    private static void scheduleJobCustom(BufferedReader in, DataOutputStream out) {
        try {
            String rply;

            // check for servers with necessary resources currently available
            serverdata[] availServers = getServersData(in, out, "available");

            // if there are servers with the required resources available, schedule to the
            // first one
            if (availServers != null) {
                // send scheduling request
                sendMessage("SCHD " + currentJob.id + " " + availServers[0].type + " " + availServers[0].id, out);
            } else { // otherwise fall back to the servers that can eventually provide the required
                     // resources
                // get capable servers
                serverdata[] capServers = getServersData(in, out, "capable");

                // find the first capable server with an estimated runtime under the threshold
                int index = 0;
                int currentEstRuntime = 0;
                do {
                    // get total estimate runtime for server
                    sendMessage("EJWT " + capServers[index].type + " " + capServers[index].id, out);

                    rply = receiveMessage(in);

                    currentEstRuntime = Integer.valueOf(rply);

                    index++;
                } while (currentEstRuntime > MAX_RUNTIME && index < capServers.length);

                // decrement index by one as it is incremented regardless of whether loop will
                // continue
                index--;

                // send scheduling request
                sendMessage("SCHD " + currentJob.id + " " + capServers[index].type + " " + capServers[index].id, out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // send a message to the server
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

    // receive message
    private static String receiveMessage(BufferedReader in) {
        try {
            String reply = in.readLine();
            return reply;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // extracts job info in useable format from a ds-server JOBN message
    private static jobinfo extractJobInfo(String msg) {
        String[] info = msg.split(" ");
        jobinfo job = new jobinfo(info[1], info[2], info[3], info[4], info[5], info[6]);
        return job;
    }

    // extracts server info in useable format from a ds-server GETS record
    private static serverdata extractServerInfo(String msg) {
        String[] info = msg.split(" ");
        serverdata server = new serverdata(info[0], info[1], info[4], info[5], info[6]);
        return server;
    }

    private static serverdata[] getServersData(BufferedReader in, DataOutputStream out, String mode) {
        String msg;
        String rply;

        // send the appropriate request based on the mode
        switch (mode) {
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