import java.util.Map;

public class Driver {

    public static void main(String[] args) {
        //TODO:
        // + make sure current functionality works for new additions to make future implementation easier
        // + implement memory management functions to support alloc algorithms
        // + implement best/worst-fit
        // + implement next-fit
    	MemoryAllocator mem = new MemoryAllocator();
    	//DAN TODO:
		//+ implement system time
		//+ subtract 1 from all allocated processes each system time
		//+ check if there are processes that are finished (procTime == 0) and release them
		boolean fin;
		int sysTime = 0;
		do {
			System.out.println("Number of processes: " + mem.procList.size());
				fin = true;
    			for(int i = 0; i < mem.procList.size(); i++) {
					
    				if(mem.first_fit(mem.procList.get(i), mem.procList.get(i).getSize()) > 0) {
    					System.out.println("[" + sysTime + "] Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
    					mem.print_status();
    				} else {
    					System.err.println("Could not allocate");
    				}
					if(!mem.procList.get(i).isFinished()) 
						fin = false;
				}
				for(Map.Entry<Process, Partition> ent : mem.allocMap.entrySet()) {
					Process p = ent.getKey();
					p.setTime(p.getTime()- 1);
				}
				sysTime++;		
		} while(!fin /*Need to add && processes that are allocated still have sysTime left */); //while processes to be allocated
    			
    }
}
