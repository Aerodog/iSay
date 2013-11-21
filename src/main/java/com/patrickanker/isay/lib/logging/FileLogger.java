package com.patrickanker.isay.lib.logging;

import com.patrickanker.isay.ISMain;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileLogger {
    
    private final File f;
    
    public FileLogger(File file) 
    {
        f = file;
    }
    
    public FileLogger(String path) 
    {
        f = new File(path);
    }
    
    public void log(String in) 
    {
        PrintWriter pw = null;
        
        try {
            if (f.exists()) {
                long size = f.length();
                
                if (size >= 10485760) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    String now = df.format(new Date());
                    
                    File _compress = new File(f.getAbsolutePath() + "." + now);
                    
                    byte[] buffer = new byte[1024];
                    
                    try {
                        FileOutputStream fos = new FileOutputStream(_compress);
                        ZipOutputStream zos = new ZipOutputStream(fos);
                        ZipEntry entry = new ZipEntry(f.getName());
                        FileInputStream fin = new FileInputStream(f);
                        
                        int len;
                        while ((len = fin.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        
                        fin.close();
                        zos.closeEntry();
                        zos.close();
                        
                        f.delete();
                        f.createNewFile();
                        
                    } catch (Throwable t) {
                        ISMain.log("Could not compress logfile: \"" + f.getAbsolutePath() + "\"", 2);
                    }
                }
            } else {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            
            pw = new PrintWriter(new FileWriter(f, true));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = df.format(new Date());
            
            pw.append("[" + now + "] " + in + "\n");
            
        } catch (IOException ex) {
            ISMain.log("Could not create new logfile: \"" + f.getAbsolutePath() + "\"", 2);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
