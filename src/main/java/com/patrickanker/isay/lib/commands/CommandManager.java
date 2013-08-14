/*
 * CommandManager.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2013. All rights reserved.
 * 
 * libpsanker by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.lib.commands;

import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.lib.logging.ConsoleLogger;
import com.patrickanker.isay.lib.permissions.InsufficientPermissionsException;
import com.patrickanker.isay.lib.permissions.PermissionsManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandManager {
    // =============================
    // - COMMAND ENGINE
    // - 
    // - Built by: psanker
    // =============================

    private String[] helpArgs = {"help", "h", "?"};
    protected Map<String, Method> aliases = new HashMap<String, Method>();
    protected Map<Method, Object> instances = new HashMap<Method, Object>();
    
    private final Plugin owningPlugin;
    
    public CommandManager(Plugin pl)
    {
        owningPlugin = pl;
    }

    public void registerCommands(Class<?> cls) 
    {
        CraftServer craftServer = (CraftServer) owningPlugin.getServer();
        List<org.bukkit.command.Command> registeredCommands = new LinkedList<org.bukkit.command.Command>();
        
        Object obj = null;

        try {
            obj = cls.newInstance();
        } catch (InstantiationException ex) {
            ConsoleLogger.getLogger(owningPlugin.getName()).log("Could not register commands from " + cls.getCanonicalName(), 2);
        } catch (IllegalAccessException ex) {
            ConsoleLogger.getLogger(owningPlugin.getName()).log("Could not register commands from " + cls.getCanonicalName(), 2);
        }
        
        if (obj == null)
            return;

        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue; // Improper command registration, helper method, or other method type
            }
            
            Command command = method.getAnnotation(Command.class);
            CommandPermission commandPermission = method.getAnnotation(CommandPermission.class);
            
            // Check for other already registered commands with this name
            if ((craftServer.getCommandMap().getCommand(command.aliases()[0]) != null) && (!ISMain.getInstance().getConfigData().getBoolean("override-other-commands"))) {
                org.bukkit.command.Command _cmd = craftServer.getCommandMap().getCommand(command.aliases()[0]);
                
                if (_cmd instanceof PluginIdentifiableCommand) {
                    PluginIdentifiableCommand _pic = (PluginIdentifiableCommand) _cmd;
                    ConsoleLogger.getLogger(owningPlugin.getName()).log("Did not register command \"" + command.aliases()[0] + "\" because of already existing command from \"" + _pic.getPlugin().getName()  + "\"", 1);
                    continue;
                }
                
                ConsoleLogger.getLogger(owningPlugin.getName()).log("Did not register command \"" + command.aliases()[0] + "\" because of already existing command from unknown source", 1);
                continue;
            }
            
            // Create Bukkit command hook
            ISayLibPluginCommand pluginCommand = new ISayLibPluginCommand(command.aliases()[0], owningPlugin);
            
            if (commandPermission != null) {
                pluginCommand.setPermission(commandPermission.value());
            } else if (commandPermission == null) {
                pluginCommand.setPermission("");
            }
            
            pluginCommand.setUsage("See \"/" + pluginCommand.getName() + " help\"");
            pluginCommand.setDescription("See \"/" + pluginCommand.getName() + " help\"");
            
            instances.put(method, obj);
            
            List<String> aliasList = new ArrayList<String>();
            for (String alias : command.aliases()) {
                String al = alias.toLowerCase();

                aliases.put(al, method);
                
                if (!al.equalsIgnoreCase(pluginCommand.getName())) {
                    aliasList.add(al);
                }
            }
            
            pluginCommand.setAliases(aliasList);
            registeredCommands.add(pluginCommand);
        }
        
        craftServer.getCommandMap().registerAll(owningPlugin.getName(), registeredCommands);
        
        // LibraryPlugin-specific call
        if (willOverride()) {
            queueCommands(registeredCommands);
        }
    }
    
    public void registerCommands(Object obj) 
    {
        if (obj == null)
            return;
        
        Class<?> cls = obj.getClass();
        CraftServer craftServer = (CraftServer) owningPlugin.getServer();
        ArrayList<org.bukkit.command.Command> registeredCommands = new ArrayList<org.bukkit.command.Command>();
        
        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue; // Improper command registration, helper method, or other method type
            }
            
            Command command = method.getAnnotation(Command.class);
            CommandPermission commandPermission = method.getAnnotation(CommandPermission.class);
            
            // Check for other already registered commands with this name
            if ((craftServer.getCommandMap().getCommand(command.aliases()[0]) != null) && (!ISMain.getInstance().getConfigData().getBoolean("override-other-commands"))) {
                org.bukkit.command.Command _cmd = craftServer.getCommandMap().getCommand(command.aliases()[0]);
                
                if (_cmd instanceof PluginIdentifiableCommand) {
                    PluginIdentifiableCommand _pic = (PluginIdentifiableCommand) _cmd;
                    ConsoleLogger.getLogger(owningPlugin.getName()).log("Did not register command \"" + command.aliases()[0] + "\" because of already existing command from \"" + _pic.getPlugin().getName()  + "\"", 1);
                    continue;
                }
                
                ConsoleLogger.getLogger(owningPlugin.getName()).log("Did not register command \"" + command.aliases()[0] + "\" because of already existing command from unknown source", 1);
                continue;
            }
            
            // Create Bukkit command hook
            ISayLibPluginCommand pluginCommand = new ISayLibPluginCommand(command.aliases()[0], owningPlugin);
            
            if (commandPermission != null && pluginCommand.getPermission() != null) {
                pluginCommand.setPermission(commandPermission.value());
            } else if (commandPermission == null & pluginCommand.getPermission() != null) {
                pluginCommand.setPermission("");
            }
            
            pluginCommand.setUsage("See \"/" + pluginCommand.getName() + " help\"");
            
            if (pluginCommand.getDescription() != null) {
                pluginCommand.setDescription("");
            }
            
            instances.put(method, obj);

            List<String> aliasList = new ArrayList<String>();
            for (String alias : command.aliases()) {
                String al = alias.toLowerCase();

                aliases.put(al, method);
                
                if (!al.equalsIgnoreCase(pluginCommand.getName())) {
                    aliasList.add(al);
                }
            }
            
            pluginCommand.setAliases(aliasList);
            registeredCommands.add(pluginCommand);
        }
        
        craftServer.getCommandMap().registerAll(owningPlugin.getName(), registeredCommands);
        
        if (willOverride()) {
            queueCommands(registeredCommands);
        }
    }

    private boolean isRegistered(String command) 
    {
        return aliases.containsKey(command.toLowerCase());
    }
    
    // LibraryPlugin-specific calls
    
    protected boolean willOverride()
    {
        return ISMain.getInstance().getConfigData().getBoolean("override-other-commands");
    }
    
    protected void queueCommands(List<org.bukkit.command.Command> commands)
    {
        ISMain.getInstance().getCommandOverrideHelper().queueCommands(commands);
    }
    
    public boolean executeCommandProcessErrors(org.bukkit.command.Command command, CommandSender cs, String[] args, Plugin owner)
    {
        try {
            executeCommand(command, cs, args);
        } catch (CommandException ex) {
            String report = new StringBuilder().append("§c").append(ex.getMessage()).toString();
            cs.sendMessage(report);

            if (((ex instanceof CommandMethodInvocationException)) || ((ex instanceof MalformattedCommandException))) {
                ConsoleLogger.getLogger(owner.getName()).log(ex.getMessage(), 2);
                return true;
            }
            if ((ex instanceof ArgumentOutOfBoundsException)) {
                try {
                    sendHelp(cs, command);
                } catch (MalformattedCommandException ex1) {
                    String _report = new StringBuilder().append("§c").append(ex1.getMessage()).toString();
                    cs.sendMessage(_report);

                    ConsoleLogger.getLogger(owner.getName()).log(ex.getMessage(), 2);
                    return true;
                }
            }
        } catch (InsufficientPermissionsException ex) {
            String report = new StringBuilder().append("§c").append(ex.getMessage()).toString();
            cs.sendMessage(report);
        }

        return true;
    }

    public void executeCommand(org.bukkit.command.Command command, CommandSender cs, String[] args) 
            throws CommandException, InsufficientPermissionsException 
    {
        // Search if command is registered
        if (!this.isRegistered(command.getName())) {
            throw new UnhandledCommandException("Unhandled command: " + command.getName());
        }

        // Get method and check to see if it matches the Command method interface
        Method method = aliases.get(command.getName());

        if (!method.isAnnotationPresent(Command.class)) {
            throw new MalformattedCommandException("Malformatted command: " + command.getName());
        }

        Command cmd = method.getAnnotation(Command.class);

        // Check out of bounds for arguments and other things like such
        boolean playerOnly = cmd.playerOnly();

        if (playerOnly && !(cs instanceof Player)) {
            throw new CommandException("Player-only command: " + command.getName());
        }

        int[] bounds = cmd.bounds();
        
        if (args.length < bounds[0] || (args.length > bounds[1] && bounds[1] >= 0)) {
            throw new ArgumentOutOfBoundsException("Argument out of bounds: " + command.getName());
        }

        if (args.length == 1 && Arrays.asList(helpArgs).contains(args[0])) {
            sendHelp(cs, command);
            return;
        }

        if (method.isAnnotationPresent(CommandPermission.class)) {
            CommandPermission perm = method.getAnnotation(CommandPermission.class);

            // -- Check if cs is player or not
            if (cs instanceof Player) {
                Player p = (Player) cs;
                
                if (!PermissionsManager.hasPermission(p.getName(), "system.admin")) {
                    if (!PermissionsManager.hasPermission(p.getName(), perm.value())) {
                        throw new InsufficientPermissionsException();
                    } 
                }     
            }
        }
        
        if (method.isAnnotationPresent(Subcommands.class)) {
            Subcommands subs = method.getAnnotation(Subcommands.class);
            
            if (cs instanceof Player) {
                Player p = (Player) cs;
                
                
                if (!PermissionsManager.hasPermission(p.getName(), "system.admin")) {
                    try {
                        if (Arrays.asList(subs.arguments()).contains(args[0])) {
                            for (int i = 0; i < subs.arguments().length; i++) {
                                if (subs.arguments()[i].equalsIgnoreCase(args[0]) && !PermissionsManager.hasPermission(p.getName(), subs.permission()[i])) {
                                    throw new InsufficientPermissionsException();
                                }
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Continue
                    }
                }
            }
        }

        // Checks clear... Run command
        Object instance = instances.get(method);
        invokeMethod(method, cs, args, instance);
    }

    private void invokeMethod(Method method, CommandSender cs, String[] args, Object instance) 
            throws CommandMethodInvocationException 
    {
        Object[] commandMethodArgs = {cs, args};

        try {
            method.invoke(instance, commandMethodArgs);
        } catch (IllegalAccessException ex) {
            throw new CommandMethodInvocationException("Internal error. Could not execute command.");
        } catch (IllegalArgumentException ex) {
            throw new CommandMethodInvocationException("Internal error. Could not execute command.");
        } catch (InvocationTargetException ex) {
            throw new CommandMethodInvocationException("Internal error. Could not execute command.");
        }
    }
    
    public void sendHelp(CommandSender cs, org.bukkit.command.Command command) 
            throws MalformattedCommandException 
    {
        Method method = aliases.get(command.getName());
        
        if (!method.isAnnotationPresent(Command.class)) {
            throw new MalformattedCommandException("Malformatted command: " + command.getName());
        }

        Command cmd = method.getAnnotation(Command.class);
        
        String help = "§6===Help: " + command.getName() + "===\n" + cmd.help() + "\n" + "§6=========================";
        
        for (String str : getMessageLines(help)) {
            cs.sendMessage(str);
        }
    }
    
    public Plugin getOwningPlugin()
    {
        return owningPlugin;
    }
    
    private static String[] getMessageLines(String message) {
        String[] split = message.split("\n");
        return split;
    }
}