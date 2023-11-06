import java.util.UUID;

public class Process {
    
    private int size; //in KB
    private int time; //in ms
    private String id;
    private boolean isFinished;

    public Process(int maxSize, int maxTime) {
        this.size = (int)(Math.random() * maxSize);
        this.time = (int)(Math.random() * maxTime);
        this.id = UUID.randomUUID().toString();
        this.isFinished = false;
    }
    
    public boolean isFinished() {
    	return isFinished;
    }
    public void setIsFinished(boolean bool) {
    	this.isFinished = bool;
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
