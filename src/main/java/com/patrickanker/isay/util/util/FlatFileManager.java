package com.patrickanker.isay.util.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FlatFileManager {
    
    private static String directory = "plugins";
    
    public static String[] load(String target) {
        return load(target, "", false);
    }
    
    public static String[] load(String target, String destination) {
        return load(target, destination, false);
    }
    
    public static String[] load(String target, String destination, boolean autoDestroy) {
        // Note: Destination is appended to ./plugins/
        // For example, when destination is "/channels",
        // the target directory will be "./plugins/channels/"
        
        File f = new File(directory + destination + "/" + target + ".txt");
        Scanner snr = null;
        
        if (f.exists()) {
            try {
                snr = new Scanner(f);
                String toCut = "";
                
                while (snr.hasNextLine()) {
                    String line = snr.nextLine();
                    
                    if (line.startsWith("#"))
                        continue;
                    
                    toCut = toCut + line + "\n";
                }
                
                snr.close();
                String[] split = toCut.split("\n");
                
                if (autoDestroy)
                    f.delete();
                
                return split;
            } catch (FileNotFoundException ex) {
                return null;
            }
        } else {
            if (!autoDestroy) {
                f.getParentFile().mkdirs();
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    return null;
                }
            }
        }
        
        return null;
    }
    
    public static void save(String[] strs, String target) {
        save(strs, target, "");
    }
    
    public static void save(String[] strs, String target, String destination) {
        // Note: Destination is appended to plugins/VoxelGuest/data
        // For example, when destination is "/channels",
        // the target directory will be "plugins/VoxelGuest/data/channels/"
        
        File f = new File(directory + destination + "/" + target + ".txt");
        PrintWriter pw = null;
        
        if (f.exists()) {
            try {
                pw = new PrintWriter(f);
                
                String concat = "";
                
                for (int i = 0; i < strs.length; i++) {
                    if (i == (strs.length - 1)) {
                        concat = concat + strs[i];
                    } else {
                        concat = concat + strs[i] + "\n";
                    }
                }
                
                pw.write(concat);
                pw.close();
            } catch (FileNotFoundException ex) {
                // Blah
            }
        }
    }
}
