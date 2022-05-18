package MyClientStage2;

public class serverDetails {
    public String type; //Server Type
    public int id; // Server Id
    public String state; // Server State
    public int startTime; //Server Current Start Time
    public int cores; //Server Cpu Cores
    public int memory; //Server Memory
    public int disk; //Server Disk

    // All details are provided by the client either by GETS CAPABLE or GETS ALL
    // serverDetails constructor
    public serverDetails(String type, int id, String state, int startTime, int cores, int memory, int disk) {
        this.type = type;
        this.id = id;
        this.state = state;
        this.startTime = startTime;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
    }
}
