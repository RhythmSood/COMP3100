package DataExtracter;

import java.util.*;

public class serverdata {
    public String type; // server type
    public String id; // server id
    public int cores; // number of cores in server
    public int memory; // server memory
    public int disk; // server disk
    public int jobs; // number of jobs (waiting or running)
    public ArrayList<Integer> estCompletionTimes = new ArrayList<Integer>(); // list of estimated completion times for
                                                                             // all jobs on the server

    public serverdata(String type, String id, String cores, String memory, String diskIn) {
        this.type = type;
        this.id = id;
        this.cores = Integer.valueOf(cores);
        this.memory = Integer.valueOf(memory);
        this.disk = Integer.valueOf(diskIn);
        estCompletionTimes.add(0);
    }

    public String toString() {
        return type + " " + id + " " + String.valueOf(cores) + " " + String.valueOf(memory) + " " + String.valueOf(disk)
                + " " + String.valueOf(jobs);
    }
}
