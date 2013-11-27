package com.patrickanker.isay.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Inspired by sk89q's command system
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * 
     * List of aliases for the command. ALWAYS SET THE PRIMARY COMMAND AS INDEX 0
     * 
     */
    String[] aliases();

    /**
     * 
     * Minimum and maximum allowed argument lengths
     * Index 0 is min number and index 1 is max number 
     * (set index 1 to -1 or lower to have unlimited arguments)
     * 
     */
    int[] bounds();

    /**
     * 
     * Prints this out when person does "/<command> ?", "/<command> help", or "/<command> h"
     * 
     */
    String help() default "§cNo help is provided for this command";

    /**
     * 
     * 
     * Checks if the command is for players only
     * 
     */
    boolean playerOnly() default false;
}
