package com.patrickanker.isay.lib.commands;

public class CommandMethodInvocationException extends CommandException {
    private static final long serialVersionUID = 9407231027666806L;

    public CommandMethodInvocationException(String reason) 
    {
        super(reason);
    }
}
