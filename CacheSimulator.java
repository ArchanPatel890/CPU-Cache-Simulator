/**
 * Archan Patel
 * apatel95@jhu.edu
 * Shiv Krishnan
 * skrish23@jhu.edu
 * 
 * Computer Systems Fundamentals
 * Assignment 7
 * @author Archanimal
 *
 */

import java.util.PriorityQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * Driver class for cache trace stats.
 */
public final class CacheSimulator {
    /** Difference for int and double comparison. */
    static final double DIFF = 0.000001;
    /** Number of size parameters from command line. */
    static final int SPARAM = 2;
    /** Number of parameters from command line. */
    static final int TPARAM = 7;
    /** Counter size MAX. */
    static final int COUNT = 4;
    /** Miss cycle time. */
    static final int MCYCLES = 100;
    /** Cache block size. */
    static int blockSize;
    /** Byte data unit size. */
    static int dataSize;
    
    /**Private constructor for utility class. */
    private CacheSimulator() {
        
    }
    
    /**
     * The main driver program.
     * @param args is the command line arguments in string format.
     */
    public static void main(String[] args) {
        int[] options = verifyInputParams(args);
        if (options == null) {
            return;
        }
        
        BufferedReader file = verifyFile(args[TPARAM - 1]);
        if (file == null) {
            return;
        }
        
        try {
            processTrace(options, file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Implementation of a block in cache, used by priorityQueue.
     */
    static final class CacheBlock implements 
        Comparable<CacheBlock> {
        /** the address tag of the block. */
        public int tag;
        /** the dirty information tag for write back. */
        public boolean v;
        /** The order the block was last used, based on an input counter. */
        public int weight;
        
        /**
         * The constructor for the CacheBlock.
         * @param t the tag address
         * @param d the dirty tag (written info)
         * @param w the pc of the access (always 0 for FIFO).
         */
        public CacheBlock(int t, boolean d, int w) {
            this.tag = t;
            this.v = d;
            this.weight = w;
        }
        
        /**
         * Returns the @return tag of the block.
         */
        public int getTag() {
            return this.tag;
        }
        
        /**
         * Returns the @return v (dirty) tag of the block.
         */
        public boolean getV() {
            return this.v;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CacheBlock)) {
                return false;
            }
            
            CacheBlock cmp = (CacheBlock) o;
            if (cmp.getTag() == this.tag) {
                return true;
            }
            
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.tag;
        }

        @Override
        public int compareTo(CacheBlock o) {
            if (this.weight > o.weight) {
                return -1;
            } else 
                if (this.weight == o.weight) {
                    return 0;
                }
            
            return 1;
        }
    }
    
    /**
     * A Class to store a line of input. 
     */
    static final class MemRW {
        /** operation being performed 0 load, 1 store. */
        private int operation;
        /** address being used for the operation. */
        private int address;
        
        /**
         * Constructor required operation and address.
         * @param op if the operation 0/1
         * @param add 32-bit address.
         */
        public MemRW(int op, int add) {
            this.operation = op;
            this.address = add;
        }
        
        /**
         * @return the operation of the line 0 (load) 1 (store).
         */
        public int getOperation() {
            return this.operation;
        }
        
        /**
         * @return the 32-bit address of the line.
         */
        public int getAddess() {
            return this.address;
        }
    }
    
    /**
     * Checks the command line input for completeness. 
     * @param args the commandline string array
     * @return an array with the parameters in number form.
     *      null if incorrect.
     */
    public static int[] verifyInputParams(String[] args) {
        int[] options = new int[TPARAM - 1];
        
        if (args.length != TPARAM) {
            System.err.println("Insufficient number of arguments.");
            return null;
        }
        
        int arg = 0;
        for (int x = 0; x <= SPARAM; x++) {
            try {
                arg = Integer.parseUnsignedInt(args[x]);
            } catch (NumberFormatException e) {
                System.err.println("Incorrect argument format.");
                return null;
            }
            
            if (!checkPowTwo(arg)) {
                System.err.println("Size argument(s) not a power of two");
                return null;
            }
            
            if (arg <= 0) {
                System.err.println("Arguments too small.");
                return null;
            }
            
            options[x] = arg;
        }
        
        for (int x = SPARAM + 1; x < TPARAM - 1; x++) {
            try {
                arg = Integer.parseUnsignedInt(args[x]);
            } catch (NumberFormatException e) {
                System.err.println("Incorrect argument format.");
                return null;
            }
            
            if (arg != 0 && arg != 1) {
                System.err.println("Incorrect write option selection.");
                return null;
            }
            
            options[x] = arg;
        }
        
        return options;
    }
    
    /**
     * Verifies whether the file can be read based on name.
     * @param name is the name of the file
     * @return a BufferedReader with the open file
     */
    public static BufferedReader verifyFile(String name) {
        BufferedReader f = null;
        
        try {
            File test = new File(name);
            f = new BufferedReader(new FileReader(test));
        } catch (IOException e) {
            System.err.println("Error. Could not find file.");
            e.printStackTrace();
        }
        
        return f;
    }
    
    /**
     * Checks whether the number is a power of two.
     * @param num is the number to be checked
     * @return boolean true if power of 2, false otherwise
     */
    public static boolean checkPowTwo(int num) {
        double log = Math.log((double) num) / Math.log(2);
        int pow = (int) log;
        if (Math.abs(log - pow) < DIFF) {
            return true;
        }
        return false;
    }
    
    /**
     * Takes a string line from trace and converts it to ints.
     * @param line from the cache trace.
     * @return a pair object of numbers for operation (0 for load
     *      1 for store) and address (32-bit).
     */
    public static MemRW parseLine(String line) {
        line = line.trim();
        String[] parse = line.split("\\s+");
        MemRW pair = null;
        
        int address;
        try {
            address = Integer.decode(parse[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error in read write trace format.");
            return null;
        }
        
        if (parse[0].equalsIgnoreCase("l")) {
            pair = new MemRW(0, address);
        } else 
            if (parse[0].equalsIgnoreCase("s")) {
                pair = new MemRW(1, address);
            }
        
        System.err.println("Incorrect read write trace format.");
        return pair;
    }
    
    /**
     * Processes a given file's cache trace line by line.
     * @param options the parameter being used in the analysis.
     * @param file a BufferedReader for the file.
     * @throws IOException if the BufferedReader fails.
     */
    public static void processTrace(int[] options, BufferedReader file) 
        throws IOException {
        assert (file.ready());
        
        int loads = 0, stores = 0, lHit = 0, stHit = 0, cycles = 0;
        int pc = 0;
        
        int a = 1; a++;
        @SuppressWarnings("unchecked")
        PriorityQueue<CacheBlock>[] cache =
                (PriorityQueue<CacheBlock>[]) new PriorityQueue[options[0]];
        blockSize = options[1];
        dataSize = options[a] / COUNT;
                
        String line = null;
        while ((line = file.readLine()) != null) {
            MemRW trace = parseLine(line);
            int addr = trace.getAddess();
            
            //trace.getOperation() 0 -> load
            if (trace.getOperation() == 0) {
                ++loads;
                int x = loadValue(cache, addr,
                        options[COUNT], options[options.length - 1], pc);
                if (x == 1) {
                    ++lHit;
                }
                
                cycles += x;
            }
            
            //trace.getOperation() 0 -> store
            if (trace.getOperation() == 1) {
                ++stores;
                int set = addr & (cache.length - 1);
                
                //Cache hit?
                if (cache[set].contains(new CacheBlock(addr, true, 0))) {
                    ++stHit;
                    cycles += storeValueHIT(cache, addr, options[COUNT], pc);
                } else { //Cache miss
                    //FIFO?
                    if (options[COUNT + 1] == 0) {
                        cycles += storeValueMFIFO(cache, addr, options[a + 1], 
                                options[COUNT]);
                    } 
                    //Least recently used.
                    if (options[COUNT + 1] == 1) {
                        cycles += storeValueMLRU(cache, addr, options[a + 1], 
                                options[COUNT], pc);
                    } 
                }
            }
            
            ++pc;
        }
        
        System.out.printf("Total loads: %d\nTotal stores: %d\n", loads, stores);
        System.out.printf("Load hits: %d\nStore hits: %d\n", lHit, stHit);
        System.out.printf("Load misses: %d\nStore misses: %d\n", loads - lHit,
                stores - stHit);
        System.out.printf("Total cycles: %d\n", cycles);
    }
    
    
    /**
     * Function that simulates a load into the cache.
     * @param cache the cache array
     * @param addr the address we are loading from
     * @param wb is the write back (0), write through (1) parameter
     * @param rep is the replacement scheme FIFO or least recent
     * @param pc the line counter for least recently used evictions
     * @return number of cycles required.
     */
    public static int loadValue(PriorityQueue<CacheBlock>[] cache,  int addr,
            int wb, int rep, int pc) {
        int cycles = 0;
        int set = addr & (cache.length - 1);
        
        CacheBlock find = new CacheBlock(addr, true, 0);
        
        //Is the cache load value a hit?
        if (cache[set].contains(find)) {
            if (rep == 1) {
                cache[set].remove(find);
                cache[set].add(new CacheBlock(addr, true, pc));
            }
            return ++cycles;
        }
        
        //is the set full? if so evict
        if (cache[set].size() == blockSize) {
            CacheBlock evict = cache[set].poll();
            
            //is the data dirty, if so we must write to memory; 100 cycles.
            if (!evict.v) {
                cycles += MCYCLES * dataSize;
            }
        }
        
        //once evicted, add the new data based on options above FIFO/ LRU
        cycles += MCYCLES * dataSize;
        if (rep == 0) {
            cache[set].add(new CacheBlock(addr, true, 0));
        }
        if (rep == 1) {
            cache[set].add(new CacheBlock(addr, true, pc));
        }
              
        ++cycles;
        return cycles;
    }
    
    /**
     * Function that simulates a store hit into the cache.
     * @param cache the cache array
     * @param addr the address we are loading from
     * @param wb is the write back (0), write through (1) parameter
     * @param pc the line counter for least recently used evictions
     * @return number of cycles required.
     */
    public static int storeValueHIT(PriorityQueue<CacheBlock>[] cache, int addr,
            int wb, int pc) {
        int cycles = 0;
        int set = addr & (cache.length - 1);
        CacheBlock find = new CacheBlock(addr, true, 0);
        
        //Write through? 101 cycles
        if (wb == 1) {
            cycles += MCYCLES * dataSize + 1;
        }
        //Write back? 1 cycle best case, 101 worst case
        if (wb == 0) {
            CacheBlock[] arr = (CacheBlock[]) cache[set].toArray();
            for (CacheBlock b: arr) {
                if (b.equals(find) && !b.v) {
                    cycles += MCYCLES * dataSize;
                }
            }
            cache[set].remove(find);
            cache[set].add(new CacheBlock(addr, false, pc));
            ++cycles;
        }
        
        return cycles;
    }
    
    /**
     * Function that simulates a store MISS with First in first out
     * eviction practice.
     * @param cache the cache array
     * @param addr the address we are loading from
     * @param wb is the write back (0), write through (1) parameter
     * @param wa is the write allocate (0), no write alloc (2).
     * @return number of cycles required.
     */
    public static int storeValueMFIFO(PriorityQueue<CacheBlock>[] cache,
            int addr, int wa, int wb) {
        int cycles = 0;
        int set = addr & (cache.length - 1);
        
        //Write allocate, write only to memory
        if (wa == 0) {
            cycles += MCYCLES * dataSize;
        }
        
        //No write allocate, write to mem and write hit
        if (wa == 1) {
            if (cache[set].size() == blockSize) {
                CacheBlock evict = cache[set].poll();
                
                if (wb == 0 && !evict.v) {
                    cycles += MCYCLES * dataSize;
                }
            }

            cycles += MCYCLES * dataSize;
            cache[set].add(new CacheBlock(addr, true, 0));
        }
        
        return cycles;
    }
    
    /**
     * Function that simulates a store MISS with least recently used
     * eviction practice.
     * @param cache the cache array
     * @param addr the address we are loading from
     * @param wb is the write back (0), write through (1) parameter
     * @param wa is the write allocate (0), no write alloc (2).
     * @param pc the line counter for least recently used evictions
     * @return number of cycles required.
     */
    public static int storeValueMLRU(PriorityQueue<CacheBlock>[] cache,
            int addr, int wa, int wb, int pc) {
        int cycles = 0;
        int set = addr & (cache.length - 1);
        
        //Write allocate
        if (wa == 0) {
            cycles += MCYCLES * dataSize;
        }
        
        //No write allocate
        if (wa == 1) {
            cycles += MCYCLES * dataSize;
            if (cache[set].size() == blockSize) {
                CacheBlock evict = cache[set].poll();
                
                if (wb == 0 && !evict.v) {
                    cycles += MCYCLES * dataSize;
                }
            }
            cache[set].add(new CacheBlock(addr, true, 0));
        }
        
        return cycles;
    }
}
