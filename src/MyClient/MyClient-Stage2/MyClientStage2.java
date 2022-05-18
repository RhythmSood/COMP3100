import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClientStage2 {
    static ArrayList<String> servers = new ArrayList<String>(); //List of servers which are idle or inactive
    static ArrayList<String> activeServers = new ArrayList<String>(); //List of servers which are active, booting or unavailable
    static int totalServers;
    static int minWaitingJobs = 0;
    static int minRunningJobs = 0;
    static String fitServerType = "";
    static int fitServerID = 0;
    
    public static void main(String[] args) {
        try {
            String username = System.getProperty("user.name");

            Socket s = new Socket("localhost",50000);  
            BufferedReader dis = new BufferedReader(new 			InputStreamReader(s.getInputStream())); //reads client message
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            jobschd(dis, dout, "HELO\n");
            jobschd(dis, dout, "AUTH" + username + "\n");

            while(true) {
                String[] jobn = jobschd(dis, dout, "REDY\n").split(" ");
                if(jobn[0].equals("NONE")) {
                    break;
                }

                if(jobn[0].equals("JOBN")){ 
                    String[] data = jobschd(dis, dout, "GETS Capable "+ jobn[4] + " " +jobn[5]+ " " +jobn[6]+ " " +" \n").split(" ");
                    totalServers = Integer.parseInt(data[1]);

                    dout.write(("OK\n").getBytes());  
                    dout.flush();            

                    for(int i = 0; i < totalServers; i++) {
                        String server = dis.readLine();
                        if(server.split(" ")[2].equals("inactive") || server.split(" ")[2].equals("idle")) {
                            servers.add(server);
                        } else {
                            activeServers.add(server);
                        }
                        System.out.println(server);
                    }

                    jobschd(dis, dout, "OK\n");

                    if(!servers.isEmpty()) {
                        for(String server : servers) {
                            String[] serverDetails = server.split(" ");

                            jobschd(dis, dout, "SCHD"+ " "+ jobn[2]+ " " + serverDetails[0] + " "+ Integer.parseInt(serverDetails[1])+"\n");

                            servers.clear();

                            break;
                        }
                    } else if(!activeServers.isEmpty()) {
                        minWaitingJobs = Integer.parseInt(activeServers.get(0).split(" ")[7]);
                        minRunningJobs = Integer.parseInt(activeServers.get(0).split(" ")[8]);
                        fitServerID = Integer.parseInt(activeServers.get(0).split(" ")[1]);
                        fitServerType = activeServers.get(0).split(" ")[0];

                        for(String server : activeServers) {
                            String[] serverDetails = server.split(" ");

                            if(serverDetails[2].equals("booting")) {
                                jobschd(dis, dout, "SCHD"+ " "+ jobn[2]+ " " + serverDetails[0] + " "+ Integer.parseInt(serverDetails[1])+"\n");
                                activeServers.clear();
                                break;
                            } else {
                                if(Integer.parseInt(serverDetails[7]) < minWaitingJobs && Integer.parseInt(serverDetails[8]) < minRunningJobs) {
                                    fitServerType = serverDetails[0];
                                    fitServerID = Integer.parseInt(serverDetails[1]);
                                    minWaitingJobs = Integer.parseInt(serverDetails[7]);
                                    minRunningJobs = Integer.parseInt(serverDetails[8]);
                                }
                            }
                        }
                        jobschd(dis, dout, "SCHD"+ " "+ jobn[2]+ " " + fitServerType + " "+ fitServerID +"\n");
                        activeServers.clear();
                    }
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
