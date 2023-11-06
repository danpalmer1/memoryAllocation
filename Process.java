import java.util.UUID;

public class Process {
    
    private int size; //in KB
    private int time; //in ms
    private int id;
    private boolean isFinished;

    public Process(int maxSize, int maxTime, int id) {
        this.size = (int)(Math.random() * maxSize);
        this.time = (int)(Math.random() * maxTime);
        this.id = id;
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
