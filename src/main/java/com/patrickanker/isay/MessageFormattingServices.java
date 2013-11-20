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

package com.patrickanker.isay;

import com.google.gson.stream.JsonReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageFormattingServices {
    
    private static final String urlFormat = "((http|ftp|https)\\:\\/\\/)(w+?\\.)?([a-zA-Z0-9\\-\\._?%&=~#])+\\.([a-zA-Z]){2,4}(\\.([a-zA-Z]){2,2})?(\\:(\\d)+)?(\\/[a-zA-Z0-9\\-\\.\\:_?%&=~#,+]*)*";
    private static final String gooGlFormat = "(http|https)\\:\\/\\/(goo)\\.(gl)\\/[a-zA-Z0-9]{5,6}";
    
    private static final String hmsFormat = "(\\d{2})(\\:(\\d{2})){2}";
    
    // --- URL Services ---
    public static boolean isURL(String in)
    {
        Pattern pattern = Pattern.compile(urlFormat);
        Matcher match = pattern.matcher(in);
        
        return (match.find() && (match.group(0).length() == in.length()));
    }
    
    public static boolean isGooGl(String in)
    {
        Pattern pattern = Pattern.compile(gooGlFormat);
        Matcher match = pattern.matcher(in);
        
        return match.find();
    }
    
    public static boolean containsURLs(String in)
    {
        Pattern pattern = Pattern.compile(urlFormat);
        Matcher match = pattern.matcher(in);
        return match.find();
    }
    
    public static String shortenURLs(String in)
    {
        Pattern urlPattern = Pattern.compile(urlFormat);
        Matcher match = urlPattern.matcher(in);
        
        while (match.find()) {
            String nextURL = match.group();
            
            if (isGooGl(nextURL)) {
                continue;
            }
            
            String shortened = getShortenedURL(nextURL);
            
            if (!("".equals(shortened))) {
                in = in.replace(nextURL, shortened);
            }
        }
        
        return in;
    }
    
    private static String getShortenedURL(final String longURL)
    {
        String shortURL = longURL;
        
        String _url = "https://www.googleapis.com/urlshortener/v1/url";
        String _content = "{\"longUrl\":\"" + longURL + "\"}";
        
        ISMain.debugLog(_content);
        
        DataOutputStream out = null;
        JsonReader in = null;
        
        try {
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(_content.getBytes().length));
            connection.setUseCaches(false);
            
            out = new DataOutputStream(connection.getOutputStream());
            
            out.writeBytes(_content);
            out.flush();
            
            in = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            in.beginObject();
            
            while (in.hasNext()) {
                String name = in.nextName();
                
                if (name.equalsIgnoreCase("id"))
                    shortURL = in.nextString();
                else
                    in.skipValue();
            }
            
        } catch (Throwable t) {
            // Custom url ... Leave it
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ISMain.log("Shortener output stream did not close properly: " + ex.getMessage(), 2);
                }
            }
            
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ISMain.log("Shortener input stream did not close properly: " + ex.getMessage(), 2);
                }
            }
            
            return shortURL;
        }
    }
    
    // -- Date services --
    public static boolean isDate(String in)
    {
        
        Pattern patWithoutDay = Pattern.compile(hmsFormat);
        Matcher match = patWithoutDay.matcher(in);

        if (!match.find(0)) {
            return false;
        } else {
            return (match.group(0).length() == in.length());
        }
    }
    
    public static boolean containsDate(String in)
    {

        Pattern patWithoutDay = Pattern.compile(hmsFormat);
        Matcher match = patWithoutDay.matcher(in);

        return (match.find(0));
    }
    
    public static String getDateString(String in)
    {
        Pattern patWithoutDay = Pattern.compile(hmsFormat);
        Matcher match = patWithoutDay.matcher(in);

        if (!match.find(0)) {
            return null;
        } else {
            String[] split = in.split("\\:");
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            int seconds = Integer.parseInt(split[2]);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            
            Calendar c = Calendar.getInstance();
            
            // Compute number of days specified in hours
            int days = (int) Math.floor(hours / 24);
            hours %= 24;
            
            try {
                c.setTime(new Date());
                c.add(Calendar.DATE, days);
                c.add(Calendar.HOUR_OF_DAY, hours);
                c.add(Calendar.MINUTE, minutes);
                c.add(Calendar.SECOND, seconds);
                
                String dt = sdf.format(c.getTime());
                return dt;
            } catch (Throwable t) {
                return null;
            }
        }
    }
}
