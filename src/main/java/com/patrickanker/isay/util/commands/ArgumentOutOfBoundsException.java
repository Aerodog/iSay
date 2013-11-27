package com.patrickanker.isay.util.commands;

public class ArgumentOutOfBoundsException extends CommandException {

    private static final long serialVersionUID = 37193865765124786L;

    public ArgumentOutOfBoundsException(String reason) 
    {
        super(reason);
    }
}
