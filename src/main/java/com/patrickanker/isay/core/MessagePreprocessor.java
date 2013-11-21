package com.patrickanker.isay.core;

import org.bukkit.entity.Player;


public abstract class MessagePreprocessor {
    
    public abstract boolean process(Player sender, String message);
    public abstract void shutdown();
    
}
