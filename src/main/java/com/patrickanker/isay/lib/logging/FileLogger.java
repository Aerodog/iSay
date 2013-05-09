/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of Overcaffeinated Development nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
