package MyClientStage2;

public class jobDetails {
    public int submitTime; //Submission Time to the server
    public int id; //Job Id
    public int estRunTime; //Estimated Run Time for the job
    public int requiredCores; //Required Cores
    public int requiredMemory; //Required Memory
    public int requiredDisk; //Required Disk

    // All details are provided by the client either by REDY -> JOBN
    //jobDetails Constructor
    public jobDetails (int submitTime, int id, int estRunTime, int requiredCores, int requiredMemory, int requiredDisk) {
        this.submitTime = submitTime;
        this.id = id;
        this.estRunTime = estRunTime;
        this.requiredCores = requiredCores;
        this.requiredMemory = requiredMemory;
        this.requiredDisk = requiredDisk;
    }
}
