public class Driver {

    public static void main(String[] args) {
        //TODO:
        // + make sure current functionality works for new additions to make future implementation easier
        // + implement memory management functions to support alloc algorithms
        // + implement best/worst-fit
        // + implement next-fit
    	MemoryAllocator mem = new MemoryAllocator();
    	//	Scanner sc = new Scanner(System.in);
		boolean fin;
		do {
			System.out.println(mem.procList.size());
				fin = true;
    			for(int i = 0; i < mem.procList.size(); i++) {
					
    				if(mem.first_fit(mem.procList.get(i), mem.procList.get(i).getSize()) > 0) {
    					System.out.println("Successfully allocated " + mem.partList.get(i).getLength() + " KB to " + mem.procList.get(i).getId());
    					mem.print_status();
    				} else {
    					System.err.println("Could not allocate");
    				}
					if(!mem.procList.get(i).isFinished()) 
						fin = false;
				}
					
		} while(!fin); //while processes to be allocated
    			
    }
}
