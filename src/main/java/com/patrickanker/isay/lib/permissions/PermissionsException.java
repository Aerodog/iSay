package com.patrickanker.isay.lib.permissions;

public class PermissionsException extends Exception {
    private static final long serialVersionUID = 22300013938599292L;

    public PermissionsException(String reason) 
    {
        super(reason);
    }
}
