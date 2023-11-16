import java.util.ArrayList;
import java.util.List;

public class Driver {

    public static void main(String[] args) {
        //TODO:
        // 	+ implement memory management functions to support alloc algorithms
        // 	+ implement best/worst-fit
        // 	+ implement next-fit
		//	+ make loop run until every process has reached 0 time
		//	+ currently issue is once all processes are allocated (alloc = true) and all processes are finished (mem.isFinished() = true), we get an infinite loop
		Driver d = new Driver();
		int f, b, w, n;
		f = d.runAlgorithm("F");
		b = d.runAlgorithm("B");
		w = d.runAlgorithm("W");
		n = d.runAlgorithm("N");
		System.out.println("First-fit: " + f + "\n"
			+ "Best-fit: " + b + "\n"
			+ "Worst-fit: " + w + "\n"
			+ "Next-fit: " + n + "\n");
}


public int runAlgorithm(String algo) {
	MemoryAllocator mem = new MemoryAllocator();
		boolean alloc = false; //init alloc to false because we need to allocate processes
		int sysTime = 0;
		while(!alloc || !mem.isFinished()) { //while processes to be allocated or processes are not finished 
			alloc = true;
			System.out.println();
			System.out.println("\n| SystemTime = " + sysTime + " |");
			for(Process proc : mem.procList) { //check if there are processes that need to be allocated in procList
				if(!proc.isAlloc()){ //if process hasn't been allocated
					alloc = false; //set variable to false
					break;
				}
			}
			if(!alloc) { //if processes still need to be allocated
				for(int i = 0; i < mem.procList.size(); i++) { //go through each process
					Process p = mem.procList.get(i);
					switch(algo) {
						case "B": 
							if(mem.best_fit(p, mem.procList.get(i).getSize()) > 0) {
								System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
							} else System.err.println("Could not allocate");
						case "W":
							if(mem.worst_fit(p, mem.procList.get(i).getSize()) > 0) {
								System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
							} else System.err.println("Could not allocate");
						case "N":
							if(mem.next_fit(p, mem.procList.get(i).getSize()) > 0) {
								System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
							} else System.err.println("Could not allocate");
						case "F":
							if(mem.first_fit(p, mem.procList.get(i).getSize()) > 0) {
								System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
							} else System.err.println("Could not allocate");
					}
				}	
    		}
			List<Process> release = new ArrayList<Process>();
			for(Process p : mem.allocMap.keySet()) {
				if(p.getTime() == 0 && p.isAlloc()){
						release.add(p);

						continue;
					}
				p.setTime(p.getTime()- 1);
			}
			for(Process p : release) {
				mem.release(p);
				System.out.println("Process " + p.getId() + " finished and was released");
			}
			mem.showResults(); //shows calculations and allocated proccess
			sysTime++;
		} 
		System.out.println("\nFINISHED");
		return sysTime;
    }
}