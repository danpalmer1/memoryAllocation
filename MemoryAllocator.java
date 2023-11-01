import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemoryAllocator {
	private int size;    // maximum memory size in bytes (B)
	private Map<String, Partition> allocMap;   // map process to partition
	private List<Partition> partList;    // list of memory partitions
    private static Map<String, Integer> configMap = new HashMap<>();
	// constructor
	public MemoryAllocator(int size) {
		this.size = size;
		this.allocMap = new HashMap<>();
		this.partList = new ArrayList<>();
		this.partList.add(new Partition(0, size)); //add the first hole, which is the whole memory at start up
	}
      
	private static Map loadConfig() {

        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into key and value
                String[] parts = line.split(" = ");

                if (parts.length == 2) {
                    String key = parts[0];
                    // Parse the value as an integer (assuming the values are integers)
                    int value = Integer.parseInt(parts[1].replaceAll("[<>]", ""));
                    configMap.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configMap;
    }

      
	// prints the allocation map (free + allocated) in ascending order of base addresses
	public void print_status() {
		order_partitions();
		System.out.printf("Partitions [Allocated=%d KB, Free=%d KB]\n", allocated_memory(), free_memory());
		for(Partition part : partList) {
			System.out.printf("Address [%d:%d] %s (%d KB)\n", 
					part.getBase(), part.getBase()+ part.getLength()-1,
					part.isbFree() ? "Free" : part.getProcess(), part.getLength());
		}
	}
      
	// get the size of total allocated memory
	private int allocated_memory() {
		//TODO: add code below
		int size = 0;
		for(Partition part : partList)
			if(!part.isbFree()) size += part.getLength();
		return size;
	}
      
	// get the size of total free memory
	private int free_memory() {
		//TODO: add code below
		int size = 0;
		for(Partition part : partList)
			if(part.isbFree()) size += part.getLength();
		return size;
	}
      
	// sort the list of partitions in ascending order of base addresses
	private void order_partitions() {
		//TODO: add code below
		Collections.sort(partList, (o1,o2) -> (o1.getBase() - o2.getBase()));
	}

	// implements the first fit memory allocation algorithm
	public int first_fit(String process, int size) {
	//TODO: add code below
		if(allocMap.containsKey(process))
			return -1; //illegal request as process has been allocated a partition already
		int index = 0, alloc = -1;
		while(index < partList.size()) {
			Partition part = partList.get(index);
			if(part.isbFree() && part.getLength() >= size) {	//found a satisfied free partition
				Partition allocPart = new Partition(part.getBase(), size);
				allocPart.setbFree(false);
				allocPart.setProcess(process);
				partList.add(index, allocPart); //insert this allocated partition at index
				allocMap.put(process, allocPart);
				part.setBase(part.getBase() + size);
				part.setLength(part.getLength() - size);
				if(part.getLength() == 0) //if the new free memory partition has 0 size -> remove it
					partList.remove(part);
				alloc = size;
				break;
			}
			index++; //try next partition
		}
		return alloc;
	}
  
	// release the allocated memory of a process
	public int release(String process) {
	//TODO: add code below
		if(!allocMap.containsKey(process))
			return -1; //no such partition allocated to process
		int size = -1;
		for(Partition part : partList) {
			if(!part.isbFree() && process.equals(part.getProcess())) {
				part.setbFree(true);
				part.setProcess(null);
				size = part.getLength();
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
	public static void main(String[] args) {
            System.out.println(loadConfig());
		}	
		
	
}
