package com.patrickanker.isay.channels;

import com.patrickanker.isay.core.channels.Channel;
import com.patrickanker.isay.core.ChatPlayer;


public class GroupChannel extends Channel {

    private final String uuid;
    
    private String nickname;
    
    public GroupChannel(String identifier)
    {
        super(identifier);
        
        uuid = identifier;
        
    }

    @Override
    public void connect(String player)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispatch(ChatPlayer player, String message)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disconnect(String player)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void load()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dump()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
