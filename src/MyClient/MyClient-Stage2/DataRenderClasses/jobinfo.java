public class jobinfo {
    public int receivedTime; // time the job was received
    public String id; // job id
    public int estRunTime; // estimated runtime of job
    public int reqCores; // number of cores required by the job
    public int reqMem; // amount of memory required by the job
    public int reqDisk; // amount of disk space required by the job

    public jobinfo(String time, String id, String runTime, String cores, String memory, String disk) {
        receivedTime = Integer.valueOf(time);
        this.id = id;
        estRunTime = Integer.valueOf(runTime);
        reqCores = Integer.valueOf(cores);
        reqMem = Integer.valueOf(memory);
        reqDisk = Integer.valueOf(disk);
    }
}
