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

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;


public class CacheSimulator {
    /** Difference for int and double comparison. */
    static final double DIFF = 0.000001;
    /** Number of size parameters from command line. */
    static final int SPARAM = 2;
    /** Number of parameters from command line. */
    static final int TPARAM = 7;
    /** Counter size MAX. */
    static final int COUNT = 4;
    
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
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    static final class MemRW {
        private int operation;
        private int address;
        
        public MemRW(int op, int add) {
            this.operation = op;
            this.address = add;
        }
        
        public int getOperation() {
            return this.operation;
        }
        
        public int getAddess() {
            return this.address;
        }
    }
    
    static final class Int {
        private int value;
        
        public Int() {
            this.value = 0;
        }
        
        public Int(int x) {
            this.value = x;
        }
        
        public void increment() {
            ++this.value;
        }
        
        public void setValue(int x) {
            this.value = x;
        }
    }
    
    
    public static int[] verifyInputParams(String[] args) {
        int[] options = new int[TPARAM];
        
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
    
    
    public static void processTrace(int[] options, BufferedReader file) 
        throws IOException {
        assert (file.ready());
        
        Int[] counter = new Int[COUNT];
        int cycles = 0;
        for (int x = 0; x < counter.length; ++x) {
            counter[x] = new Int();
        }
        
        int a = 0;
        int[][] cache = new int[options[a]][options[++a]];
        
        String line = null;
        while ((line = file.readLine()) != null) {
            MemRW trace = parseLine(line);
            if (trace.getOperation() == 0) {
                cycles += loadValue(cache, counter[0], trace.getAddess(),
                            options[options.length - 1]);
            }
            
            
        }
    }
    
    /**
     * Function that simulates a load into the cache.
     * @param cache the cache array
     * @param counter an Int counter reference
     * @param addr the address we are loading from
     * @param rep is the replacement scheme FIFO or least recent
     * @return number of cycles required.
     */
    public static int loadValue(int[][] cache, Int counter, int addr, int rep) {
        counter.increment();
        
        return 0;
    }
    
    
    

}
