/*
 * MessagePreprocessingHandler.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2013. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core;

import com.patrickanker.isay.ISMain;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;


public class MessagePreprocessingHandler {
    
    private List<MessagePreprocessor> highestProcesses = new LinkedList<MessagePreprocessor>();
    private List<MessagePreprocessor> highProcesses = new LinkedList<MessagePreprocessor>();
    private List<MessagePreprocessor> normalProcesses = new LinkedList<MessagePreprocessor>();
    private List<MessagePreprocessor> lowProcesses = new LinkedList<MessagePreprocessor>();
    private List<MessagePreprocessor> lowestProcesses = new LinkedList<MessagePreprocessor>();
    
    private STATUS status = STATUS.OK;
    
    public void registerProcess(Class<? extends MessagePreprocessor> clazz, IMPORTANCE importance)
    {
        try {
            MessagePreprocessor processor = (MessagePreprocessor) clazz.newInstance();
            
            switch (importance) {
                case HIGHEST:
                    if (!highestProcesses.contains(processor))
                        highestProcesses.add(processor);
                case HIGH:
                    if (!highProcesses.contains(processor))
                        highProcesses.add(processor);
                case NORMAL:
                    if (!normalProcesses.contains(processor))
                        normalProcesses.add(processor);
                case LOW:
                    if (!lowProcesses.contains(processor))
                        lowProcesses.add(processor);
                case LOWEST:
                    if (!lowestProcesses.contains(processor))
                        lowestProcesses.add(processor);
            }
        } catch (Throwable t) {
            ISMain.log("Attempted to register anomalous Message Preprocessor", 1);
        }
    }
    
    public STATUS getStatus()
    {
        return status;
    }
    
    public void process(Player sender, String message)
    {
        for (Object obj : highestProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            
            if (!_process.process(sender, message)) {
                status = STATUS.TERMINATED;
                break;
            }
        }
        
        if (status == STATUS.TERMINATED)
            return;
        
        for (Object obj : highProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            
            if (!_process.process(sender, message)) {
                status = STATUS.TERMINATED;
                break;
            }
        }
        
        if (status == STATUS.TERMINATED)
            return;
        
        for (Object obj : normalProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            
            if (!_process.process(sender, message)) {
                status = STATUS.TERMINATED;
                break;
            }
        }
        
        if (status == STATUS.TERMINATED)
            return;
        
        for (Object obj : lowProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            
            if (!_process.process(sender, message)) {
                status = STATUS.TERMINATED;
                break;
            }
        }
        
        for (Object obj : lowestProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            
            if (!_process.process(sender, message)) {
                status = STATUS.TERMINATED;
                break;
            }
        }
    }
    
    public void terminateProcesses()
    {
        for (Object obj : highestProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            _process.shutdown();
        }
        
        for (Object obj : highProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            _process.shutdown();
        }
        
        for (Object obj : normalProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            _process.shutdown();
        }
        
        for (Object obj : lowProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            _process.shutdown();
        }
        
        for (Object obj : lowestProcesses) {
            MessagePreprocessor _process = (MessagePreprocessor) obj;
            _process.shutdown();
        }
    }
    
    public enum IMPORTANCE
    {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }
    
    public enum STATUS
    {
        OK,
        TERMINATED
    }
}
