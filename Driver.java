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
		//+ may run into issues with having process be the allocMap key
		//+ make look run until every process has reached 0 time
		//+ check if there are processes that are finished (procTime == 0) and release them
		boolean alloc;
		int sysTime = 0;
		do {
			System.out.println("Number of processes: " + mem.procList.size());
			alloc = true;
			System.out.println("SysTime= " + sysTime);
			
			for(Process proc : mem.procList) {
				if(!proc.isAlloc()){
					alloc = false;
				}
			}
			if(!alloc) {
    		for(int i = 0; i < mem.procList.size(); i++) {		
    			if(mem.first_fit(mem.procList.get(i), mem.procList.get(i).getSize()) > 0) {
    				System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
    			} else System.err.println("Could not allocate");
    		}	
    	}
    		
			for(Map.Entry<Process, Partition> ent : mem.allocMap.entrySet()) {
				Process p = ent.getKey();
				if(p.getTime() == 0){
					mem.release(p);
					break;
				} else {
					p.setTime(p.getTime()- 1);
				}
			}
//			mem.print_status();
			mem.showResults(); //shows calculations
			sysTime++;		
		} while(!alloc || !mem.isFinished()/*Need to add && processes that are allocated still have sysTime left */); //while processes to be allocated
		System.out.println("FINISHED");
    }
}
