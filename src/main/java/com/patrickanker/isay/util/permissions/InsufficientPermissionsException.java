package com.patrickanker.isay.util.permissions;

public class InsufficientPermissionsException extends PermissionsException {
    private static final long serialVersionUID = 20814324162432463L;
    
    public InsufficientPermissionsException() 
    {
        super("You do not have sufficient privileges to access this command.");
    }   
}
