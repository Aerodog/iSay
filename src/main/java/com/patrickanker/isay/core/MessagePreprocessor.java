/*
 * MessagePreprocessor.java
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

import org.bukkit.entity.Player;


public abstract class MessagePreprocessor {
    
    public abstract boolean process(Player sender, String message);
    public abstract void shutdown();
    
}
