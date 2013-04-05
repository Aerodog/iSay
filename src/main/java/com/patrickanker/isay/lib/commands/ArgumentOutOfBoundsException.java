/*
 * ArgumentOutOfBoundsException.java
 *
 * Project: libpsanker
 *
 * Copyright (C) Patrick Anker 2011 - 2013. All rights reserved.
 * 
 * libpsanker by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.lib.commands;

public class ArgumentOutOfBoundsException extends CommandException {

    private static final long serialVersionUID = 37193865765124786L;

    public ArgumentOutOfBoundsException(String reason) 
    {
        super(reason);
    }
}
