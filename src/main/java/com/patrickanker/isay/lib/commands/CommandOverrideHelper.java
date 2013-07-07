/*
 * CommandOverrideHelper.java
 *
 * Project: libpsanker
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * libpsanker by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.lib.commands;

import com.patrickanker.isay.ISMain;
import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_6_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;


public final class CommandOverrideHelper implements Listener {
    
    private CommandOverrider overrider;
    
    public CommandOverrideHelper()
    {
        overrider = new CommandOverrider();
        Bukkit.getPluginManager().registerEvents(this, ISMain.getInstance());
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPluginEnable(PluginEnableEvent event)
    {
        if (!overrider.isEmpty()) {
            overrider.addPlugin(event.getPlugin());
        } else {
            overrider.fillPlugins();
        }
        
        int length = 0;
        
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.isEnabled())
                ++length;
        }
        
        if (length == Bukkit.getPluginManager().getPlugins().length) {
            overrider.override();
        }
    }
    
    protected void queueCommands(final List<org.bukkit.command.Command> commands)
    {
        synchronized (overrider.queued) {
            for (org.bukkit.command.Command command : commands) {
                
                if (!(command instanceof ISayLibPluginCommand))
                    continue;
                
                ISayLibPluginCommand cmd = (ISayLibPluginCommand) command;
                
                if (!overrider.queued.contains(cmd)) {
                    overrider.queued.add(cmd);
                }
            }
        }
    }
    
    class CommandOverrider {
        
        private final List<ISayLibPluginCommand> queued = new ArrayList<ISayLibPluginCommand>();
        
        private List<Plugin> plugins = new ArrayList<Plugin>();
        private SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        
        synchronized void fillPlugins()
        {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.isEnabled()) {
                    plugins.add(plugin);
                }
            }
        }
        
        synchronized void addPlugin(Plugin plugin)
        {
            if (!plugins.contains(plugin))
                plugins.add(plugin);
        }
        
        synchronized boolean isEmpty()
        {
            return plugins.isEmpty();
        }
        
        synchronized void override()
        {
            HashMap<Plugin, List<org.bukkit.command.Command>> toRegister = new HashMap<Plugin, List<org.bukkit.command.Command>>();
            Collection<org.bukkit.command.Command> _collect = commandMap.getCommands();
            
            for (ISayLibPluginCommand command : queued) {
                if (!toRegister.containsKey(command.getPlugin())) {
                    List<org.bukkit.command.Command> l = new ArrayList<org.bukkit.command.Command>();
                    l.add(command);
                    toRegister.put(command.getPlugin(), l);
                } else {
                    List<org.bukkit.command.Command> l = toRegister.get(command.getPlugin());
                    l.add(command);
                    toRegister.put(command.getPlugin(), l);
                }
            }
            
            for (Iterator<Command> it = _collect.iterator(); it.hasNext();) {
                org.bukkit.command.Command _command = it.next();

                if (!(_command instanceof PluginIdentifiableCommand))
                    continue;
                
                if (_command instanceof ISayLibPluginCommand)
                    continue;
                
                PluginIdentifiableCommand pic = (PluginIdentifiableCommand) _command;
                
                boolean overridingName = false;
                
                for (Iterator<ISayLibPluginCommand> itpc = queued.iterator(); itpc.hasNext();) {
                    ISayLibPluginCommand pc = itpc.next();
                    
                    if (pc.getAliases().contains(_command.getName())) {
                        overridingName = true;
                        break;
                    }
                }
                
                if (overridingName)
                    continue;

                List<String> aliases = _command.getAliases();

                for (int i = 0; i < aliases.size(); ++i) {
                    String alias = aliases.get(i);

                    for (Iterator<ISayLibPluginCommand> itpc = queued.iterator(); itpc.hasNext();) {
                        ISayLibPluginCommand pc = itpc.next();

                        if (pc.getAliases().contains(alias)) {
                            aliases.remove(alias);
                            break;
                        }
                    }
                }

                _command.setAliases(aliases);

                if (!toRegister.containsKey(pic.getPlugin())) {
                    List<org.bukkit.command.Command> l = new ArrayList<org.bukkit.command.Command>();
                    l.add(_command);
                    toRegister.put(pic.getPlugin(), l);
                } else {
                    List<org.bukkit.command.Command> l = toRegister.get(pic.getPlugin());
                    l.add(_command);
                    toRegister.put(pic.getPlugin(), l);
                }
            }
            
            commandMap.clearCommands();
            
            for (Iterator<Entry<Plugin, List<org.bukkit.command.Command>>> it = toRegister.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Plugin, List<org.bukkit.command.Command>> entry = it.next();
                commandMap.registerAll(entry.getKey().getName(), entry.getValue());
            }
        }
    }
}
