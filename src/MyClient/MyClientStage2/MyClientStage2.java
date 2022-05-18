//Imports
import java.util.*;
import java.io.*;
import java.net.*;

public class MyClientStage2 {

    //Testing variables
    serverDetails firstServer;

    //serverDetails object being instiated
    //Holds all servers that are taken in initially
	private ArrayList<serverDetails> serverArrList = new ArrayList<serverDetails>();

    //Holds the initial serverDetails information for first fit algorithm
    private ArrayList<serverDetails> initialServerArrList = new ArrayList<serverDetails>();

    //String Array will hold the current jobDetails that is being given
    String[] splitJobInfo;

    //String Array will hold the current serverDetails that is being given
    String[] splitServerInfo;

    //jobDetails object being instiated
    //Holds all Jobs
	private ArrayList<jobDetails> jobArrList = new ArrayList<jobDetails>();

    //Holds a list of all the biggest Servers
    private ArrayList<serverDetails> bigServerArr = new ArrayList<serverDetails>();

    //Keeps track of the Current serverDetails id, crucial to performing Largest Round Robin
    int currServerId = 0;

    //Keeps track of the current jobDetails id
    int currJobId = -1;

    //String to identify the biggest serverDetails Type
    String biggestServerType = null;

    //Variable to keep track of the number of big servers
    int biggestServerCount = 0;

    //Holds the highest serverDetails core count
    int highestServerCores = 0;

    //We need to keep track of the last string that has been sent 
    //to the client in order to know how to respond to the serverDetails
    String lastRcvdMessage = " ";

    //Constructor - Passing Target Ip Address and Port
    public Client(String targetIP, int port, String algorithm) {
        try {
            //Making a Socket
            socket = new Socket(targetIP, port);
            
            //Responsible for recieving the message from the serverDetails
            in = new DataInputStream( socket.getInputStream() );

            //Responsible for sending the message to the serverDetails
            out = new DataOutputStream( socket.getOutputStream() );
        
            //Initial Handshake - Establishes the connection
            
            //Client Sends 'HELO' to the serverDetails, commences communication
            sendMessage("HELO\n");

            //serverDetails Sends 'OK' to the client, acknowledges the 'HELO'
            rcvdMessage();

            //Client Sends Authentication and the System User to the serverDetails
            sendMessage("AUTH " + System.getProperty("user.name")+ "\n");

            //serverDetails Sends Authentication Acknowledgement to Client - 'OK'
            rcvdMessage();

            //If the message received is 'NONE' go to QUIT - No more Jobs
            //If not enter the loop
            while (!lastRcvdMessage.contains("NONE")) {

                //Sending 'REDY' to the serverDetails to show we are ready
                //for the next message
                sendMessage("REDY\n");

                //Receiving the reply from the serverDetails
                rcvdMessage();

                //if we received a 'JOBN' enter the if statement
                if (lastRcvdMessage.contains("JOBN")) {
                    //We have a jobDetails to schedule!

                    //Splitting jobDetails info when it is recieved
                    splitJobInfo = lastRcvdMessage.split(" ");

                    //jobDetails Information
                    //Passing the jobDetails attributes to make a 'jobDetails' Object
                    //which is put into the 'jobArrList' ArrayList
                    jobArrList.add(new jobDetails(
                        Integer.parseInt(splitJobInfo[1]), //jobDetails Submit Time
                        Integer.parseInt(splitJobInfo[2]), //jobDetails Id
                        Integer.parseInt(splitJobInfo[3]), //jobDetails Estimated Run Time
                        Integer.parseInt(splitJobInfo[4]), //jobDetails Cpu Cores
                        Integer.parseInt(splitJobInfo[5]), //jobDetails Memory
                        Integer.parseInt(splitJobInfo[6]) //jobDetails Disk
                    ));

                    //Increment the jobDetails, this allows us to retrieve the jobDetails from the jobArrList
                    currJobId++;

                    //If serverArrList is empty enter, else only retrieve a list of capable servers
                    //GETS All takes all the servers received from the serverDetails
                    if (initialServerArrList.isEmpty()) {
                        sendMessage("GETS All " + splitJobInfo[4] + " " + 
                        splitJobInfo[5] + " " + splitJobInfo[6] + "\n");
                    } else {
                        sendMessage("GETS Capable " + splitJobInfo[4] + " " + 
                        splitJobInfo[5] + " " + splitJobInfo[6] + "\n");
                    }

                    //Prep Data sent in
                    rcvdMessage();

                    //Splitting the Data that was sent from the serverDetails
                    String[] splitDataInfo = lastRcvdMessage.split(" ");

                    //Saving the length of data sent from the serverDetails into an 'int' variable
                    //In otherwords, How many servers to be expected from the serverDetails
                    int lengthOfData = Integer.parseInt(splitDataInfo[1]);

                    //Acknowledge Preperation Data being Sent in
                    sendMessage("OK\n");

                    for (int i = 0; i < lengthOfData; i++) {
                        //serverDetails Info being sent in
                        rcvdMessage();

                        //Spliting serverDetails info from the received message
                        splitServerInfo = lastRcvdMessage.split(" ");

                        //Passing the serverDetails attributes to make a 'serverDetails' Object
                        //which is put into the 'serverArrList' ArrayList
                        serverArrList.add(new serverDetails(
                            splitServerInfo[0], //serverDetails Type
                            Integer.parseInt(splitServerInfo[1]), //serverDetails Id
                            splitServerInfo[2], //serverDetails State
                            Integer.parseInt(splitServerInfo[3]), //serverDetails Current Start Time
                            Integer.parseInt(splitServerInfo[4]), //serverDetails Coy Cores
                            Integer.parseInt(splitServerInfo[5]), //serverDetails Memory
                            Integer.parseInt(splitServerInfo[6]) //serverDetails Disk
                        ));
                        if (i == 0) {
                            firstServer = serverArrList.get(0);
                        }
                    }

                    //Storing ALL the servers in a seperate serverDetails object once at the start
                    if (initialServerArrList.isEmpty()) {
                        initialServerArrList.add(new serverDetails(
                            splitServerInfo[0], //serverDetails Type
                            Integer.parseInt(splitServerInfo[1]), //serverDetails Id
                            splitServerInfo[2], //serverDetails State
                            Integer.parseInt(splitServerInfo[3]), //serverDetails Current Start Time
                            Integer.parseInt(splitServerInfo[4]), //serverDetails Coy Cores
                            Integer.parseInt(splitServerInfo[5]), //serverDetails Memory
                            Integer.parseInt(splitServerInfo[6]) //serverDetails Disk
                        ));
                    }

                    //Sending 'OK' to the serverDetails
                    sendMessage("OK\n");

                    //Receiving a '.' signifies there is no more information
                    //to be sent regarding serverDetails information
                    rcvdMessage();

                    //Calling First Capable Algorithm
                    if (algorithm.equals("fc")) {
                        firstCapable();
                    //Calling Largest Round Robin Algorithm
                    } else if (algorithm.equals("lrr")) {
                        largestRoundRobin();
                    //Calling First Fit Algorithm
                    } else if (algorithm.equals("ff")) {
                        firstFit();
                    } else if (algorithm.equals("lc")) {
                        lowCost();
                    }

                    //Clearing the serverDetails Array List to free up space for the next list of servers
                    //Also to get the most current serverDetails information
                    serverArrList.clear();

                    //Receiving 'OK' from the serverDetails who is
                    //acknowledging the scheduled jobDetails
                    rcvdMessage();
                }
            } 
            
        //If we have made it to this point it means all the jobDetails-scheduling
        //is done and the client is now reading to send 'QUIT' to the serverDetails
        //to end the application
        sendMessage("QUIT\n");

        //Catching any and all exceptions that are thrown
        } catch (UnknownHostException uhe) {
            System.out.println("UnknownHostException: " + uhe.getMessage());
        } catch (EOFException eof) {
            System.out.println("EOFException:" + eof.getMessage());
        } catch (IOException io) {
            System.out.println("IOException" + io.getMessage());
        } finally {
            if(socket != null) {
                try {
                    //Closing the socket
                    socket.close();
                } catch (IOException io) {
                    System.out.println("Close:" + io.getMessage());
                }
            }
        }
    }

    //Main Function to call the Client object to run it
    public static void main (String args[]) {
        Socket s = new Socket("localhost",50000);  
        BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream())); //reads client message
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    }

    //Refactored Utility Functions: 'sendMessage()' and 'rcvdMessage()'

    //Send Method Refactored
    //Purpose: Allows the Client to Send Messages to the serverDetails
    public void sendMessage(String outMessage) throws IOException {
        //Translating the string into bytes to be sent
        out.write(outMessage.getBytes("UTF-8"));

        //Prints to Terminal the messages sent by the Client
        //System.out.println("Sent: " + outMessage);
        
        //Ensuring all bytes are sent out 
        out.flush();
    }

    //Recieved Method Refactored
    //Purpose: Allows the Client to Recieve messages from the serverDetails
    public String rcvdMessage() throws IOException {

        //String to be returned by the method
        String inMessage = null;

        //Reading in the message to the 'inMessage' String
        //Using depracted method which was allowed by tutor
        inMessage = in.readLine();
            
        //Prints to Terminal the serverDetails Sent Messages
        //System.out.println("RCVD: " + inMessage);
            
        //Everytime 'rcvdMessage()' is called we are
        //updating the last message to keep track of it
        lastRcvdMessage = inMessage;

        //Returning the received message
        return inMessage;
    }

    //First Fit Algorithm
    public void firstFit() throws IOException {

        //Checking only the servers recieved by 'GETS Capable'
        for (serverDetails serv: serverArrList) {
            if (Integer.parseInt(splitJobInfo[4]) <= serv.cores && Integer.parseInt(splitJobInfo[5]) <= serv.memory 
            && Integer.parseInt(splitJobInfo[6]) <= serv.disk && !serv.state.equals("unavailable")) {
                sendMessage("SCHD " + splitJobInfo[2] + " " + serv.type + " " + serv.id+ "\n");
                return;
            }
        }

        //Checking all the servers
        for (serverDetails serv: initialServerArrList) {
            //Variable to hold the Temporary serverDetails
            serverDetails tempServ;
            if (Integer.parseInt(splitJobInfo[4]) <= serv.cores && Integer.parseInt(splitJobInfo[5]) <= serv.memory 
            && Integer.parseInt(splitJobInfo[6]) <= serv.disk && !serv.state.equals("unavailable")) {
                //Setting the temporary serverDetails as the found serverDetails
                tempServ = serv;
                //Changing the id to zero, otherwise the serverDetails will think it does not exist
                tempServ.id = 0;
                sendMessage("SCHD " + splitJobInfo[2] + " " + tempServ.type + " " + tempServ.id+ "\n");
                return;
            }
        }
    }
}