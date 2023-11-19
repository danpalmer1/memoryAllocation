import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryAllocator {
	private int size;    // maximum memory size in bytes (B)
	Map<Process, Partition> allocMap;   // map process to partition
	List<Partition> partList;    // list of memory partitions
    private static Map<String, Integer> configMap = new HashMap<>();
	List<Process> procList = new ArrayList<Process>();
    private int lastAllocatedIndex; // Start searching from the last allocated index

	// constructor
	public MemoryAllocator() {
		loadConfig(); //load config info into configMap
		size = configMap.get("MEMORY_MAX");
		this.allocMap = new HashMap<>();
		this.partList = new ArrayList<>();
		this.partList.add(new Partition(0, size)); //add the first hole, which is the whole memory at start up
		//create NUM_PROC processes
		// for(int i = 0; i < configMap.get("NUM_PROC"); i++) {
		// 	Process proc = new Process(configMap.get("PROC_SIZE_MAX"), configMap.get("MAX_PROC_TIME"), i);
		// 	procList.add(proc);
		// }
		Process proc0 = new Process(16, 42, 0);
		Process proc1 = new Process(55, 12, 1);
		Process proc2 = new Process(42, 10, 2);
		Process proc3 = new Process(18, 52, 3);
		Process proc4 = new Process(70, 71, 4);

		procList.add(proc0);
		procList.add(proc1);
		procList.add(proc2);
		procList.add(proc3);
		procList.add(proc4);


	}
      
	private static Map<String, Integer> loadConfig() {
		//put default values
		// configMap.put("MEMORY_MAX", 1024);
		// configMap.put("PROC_SIZE_MAX", 256);
		// configMap.put("NUM_PROC", 10);
		// configMap.put("MAX_PROC_TIME", 10000);
		try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into key and value
                String[] parts = line.split(" = ");
                if (parts.length == 2) {
                    String key = parts[0].replaceAll("[<>]", "");
                    // Parse the value as an integer (assuming the values are integers)
                    int value = Integer.parseInt(parts[1].replaceAll("[<>]", ""));
                    configMap.put(key, value);
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
			ioe.printStackTrace();
		}
        return configMap;
    }
		
	// get the size of total allocated memory
	private int allocated_memory() {
		int size = 0;
		for(Partition part : partList)
			if(!part.isbFree()) size += part.getLength();
		return size;
	}
      
	// get the size of total free memory
	private int free_memory() {
		int size = 0;
		for(Partition part : partList)
			if(part.isbFree()) size += part.getLength();
		return size;
	}
      
	// sort the list of partitions in ascending order of base addresses
	private void order_partitions() {
		Collections.sort(partList, (o1,o2) -> (o1.getBase() - o2.getBase()));
	}

	// implements the first fit memory allocation algorithm
	public int first_fit(Process proc, int size) {
		if(allocMap.containsKey(proc))
			return -1; //illegal request as process has been allocated a partition already
		int index = 0, alloc = -1;
		while(index < partList.size()) {
			Partition part = partList.get(index);
			if(part.isbFree() && part.getLength() >= size) {	//found a satisfied free partition
				Partition allocPart = new Partition(part.getBase(), size);
				allocPart.setbFree(false);
				allocPart.setProcess(proc);
				partList.add(index, allocPart); //insert this allocated partition at index
				allocMap.put(proc, allocPart);
				part.setBase(part.getBase() + size);
				part.setLength(part.getLength() - size);
				if(part.getLength() == 0) //if the new free memory partition has 0 size -> remove it
					partList.remove(part);
				alloc = size;
				//procList.remove(proc);
				proc.setIsAlloc(true);
				break;
			}
			index++; //try next partition
		}
		return alloc;
	}
public int next_fit(Process proc, int size){
		if (allocMap.containsKey(proc))
        return -1; // Illegal request as the process has been allocated a partition already

    int alloc = -1;
    int index = lastAllocatedIndex; // Start searching from the last allocated index

    do {
        Partition part = partList.get(index);

        if (part.isbFree() && part.getLength() >= size) {
            // Found a satisfied free partition
            Partition allocPart = new Partition(part.getBase(), size);
            allocPart.setbFree(false);
            allocPart.setProcess(proc);
            partList.add(index, allocPart); // Insert this allocated partition at index
            allocMap.put(proc, allocPart);
            part.setBase(part.getBase() + size);
            part.setLength(part.getLength() - size);

            if (part.getLength() == 0) // If the new free memory partition has 0 size -> remove it
                partList.remove(part);

            alloc = size;
            proc.setIsAlloc(true);
            lastAllocatedIndex = index; // Update the last allocated index
            break;
        }

        index = (index + 1) % partList.size(); // Move to the next partition in a circular manner

    } while (index != lastAllocatedIndex); // Continue until we complete one full iteration

    return alloc;



	}
	// implements the best fit memory allocation algorithm
	public int best_fit(Process proc, int size) {
		if(allocMap.containsKey(proc))
			return -1; //illegal request as process has been allocated a partition already
		int index = 0, alloc = -1; Partition candidatePart = null; int holeLeft = 99999999;
		//iterate through partition list and find the partition nearest in size to select
		for(Partition part : partList) {
			if(part.isbFree() && part.getLength() >= size && part.getLength() - size <= holeLeft) { //found candidate partition
				candidatePart = part;
				holeLeft = part.getLength() - size;
				alloc = size;
			}
		}
		if(alloc != -1) { //valid partition found
			Partition allocPart = new Partition(candidatePart.getBase(), size);
			allocPart.setbFree(false);
			allocPart.setProcess(proc);
			partList.add(index, allocPart); //insert this allocated partition at index
			allocMap.put(proc, allocPart);
			candidatePart.setBase(candidatePart.getBase() + size);
			candidatePart.setLength(candidatePart.getLength() - size);
			if(candidatePart.getLength() == 0) //if the new free memory partition has 0 size -> remove it
				partList.remove(candidatePart);
			proc.setIsAlloc(true);
		} 
		return alloc;
	}
	//check that the currently allocated processes have time left
	public boolean isFinished() {
		//if(allocMap.size() < 2) return false;
		for(Map.Entry<Process, Partition> ent : allocMap.entrySet()) {
			Process p = ent.getKey();
			if(p.getTime() > 0) {
				return false;
			}
		}
		return true;
	}  
	// release the allocated memory of a process
	public int release(Process process) {
		if(!allocMap.containsKey(process))
			return -1; //no such partition allocated to process
		int size = -1;
		for(Partition part : partList) {
			if(!part.isbFree() && process.equals(part.getProcess())) {
				part.setbFree(true);
				part.setProcess(null);
				size = part.getLength();
				allocMap.remove(process);
				break;
			}
		}
		if(size < 0) 
			return size;
		merge_holes(); //merge nearby free memory partitions together
		return size;
	}      
  
	// procedure to merge adjacent holes
	private void merge_holes() {
		order_partitions(); //sort the partitions based on increasing base addresses
		int i = 0;
		while(i < partList.size()) {
			Partition part = partList.get(i);
			if(part.isbFree()) {
				int endAddr = part.getBase() + part.getLength()-1;
				int j = i + 1;
				while(j < partList.size() && partList.get(j).isbFree()) {
					//merge partition j into partition i
					int start_j = partList.get(j).getBase();
					if(start_j == endAddr + 1) {
						//increase length of part i
						part.setLength(part.getLength() + partList.get(j).getLength());
						partList.remove(partList.get(j)); //remove partition j from the list
					}
					j++; //try next partition to see if it can be merged into partition i or not
				}
			}
			i++; //try next partition to merge all available free partitions
		}
	}
	
	//public method to access configMap
	public Map<String, Integer> getConfigMap() {
		return configMap;
	}

	public void showResults() {
		double num_holes = 0;
		double sum_holes = 0;
		order_partitions();
		System.out.print("| ");

		for(Map.Entry<Process, Partition> ent : allocMap.entrySet()) {
			Process p = ent.getKey();
			System.out.print("P" + p.getId() + " [" +
			p.getTime() + "s] " + "(" + p.getSize()
			+ " KB) | ");
		
		}
		System.out.println("Free (" + free_memory() + " KB) |");

		
		for(int i = 0; i < partList.size(); i++) {
			if(!partList.get(i).isbFree()) {
				num_holes++;
				sum_holes += partList.get(i).getLength();
			}
		}

		DecimalFormat df = new DecimalFormat("#.####");
		double avg_size = sum_holes/num_holes;
		double percent = (num_holes/size) * 100;
		
		System.out.print("| Free Holes (" + num_holes + ") | " + "Avg Size (" +
		avg_size + " KB) | " + "Total Size (" + sum_holes + " KB)"
		+ " | Percent (" + df.format(percent) + "%) |");
		
	}
}
