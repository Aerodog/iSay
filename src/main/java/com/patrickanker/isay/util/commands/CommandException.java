package com.patrickanker.isay.util.commands;

public class CommandException extends Exception {
    private static final long serialVersionUID = 17124527707790318L;

    public CommandException(String reason) 
    {
        super(reason);
    }
}
