public class Process {
    
    private int size; //in KB
    private int time; //in ms
    private int id;
    private boolean isAlloc;

    public Process(int maxSize, int maxTime, int id) {
        this.size = (int)(Math.random() * maxSize);
        this.time = (int)(Math.random() * maxTime);
        this.id = id;
        this.isAlloc = false;
    }
    
    public boolean isAlloc() {
    	return isAlloc;
    }
    public void setIsAlloc(boolean bool) {
    	this.isAlloc = bool;
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
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
