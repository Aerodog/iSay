package com.patrickanker.isay.util.commands;

import com.patrickanker.isay.util.permissions.PermissionsManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ISayLibPluginCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

    private final Plugin owner;
    private CommandExecutor executor;
    
    protected ISayLibPluginCommand(final String name, Plugin owner)
    {
        super(name);
        this.executor = owner;
        this.owner = owner;
        this.usageMessage = "";
    }
    
    @Override
    public boolean execute(CommandSender cs, String label, String[] args) throws org.bukkit.command.CommandException
    {
        boolean success = false;
        
        if (!owner.isEnabled()) {
            return false;
        }
        
        if (!(testPermission(cs))) {
            return true;
        }
        
        try {
            success = this.executor.onCommand(cs, this, label, args);
        } catch (ArrayIndexOutOfBoundsException ex) { // Temporary fix
            cs.sendMessage("§cArgument out of bounds: " + label);
        } catch (Throwable t) {
            throw new org.bukkit.command.CommandException("Uncaught exception \"" + t.toString() + "\" while parsing command \"" + label + "\" in plugin \"" + owner.getName() + "\"");
            
        }
        
        if ((!success) && (this.usageMessage.length() > 0)) {
            for (final String _line : this.usageMessage.replace("<command>", label).split("\n")) {
                cs.sendMessage(_line);
            }
        }
        
        return success;
    }
    
    public CommandExecutor getExecutor()
    {
        return executor;
    }
    
    public void setExecutor(CommandExecutor commandExecutor) {
        executor = commandExecutor;
    }

    @Override
    public Plugin getPlugin()
    {
        return owner;
    }
    
    @Override
    public boolean testPermissionSilent(final CommandSender sender)
    {
        if (getPermission() == null || getPermission().length() == 0)
            return true;
        
        if (sender instanceof Player) {
            Player p = (Player) sender;
            
            for (String str : getPermission().split(";")) {
                if (PermissionsManager.hasPermission(p.getWorld().getName(), p.getName(), str)) {
                    return true;
                }
            }
        } else {
            for (String str : getPermission().split(";")) {
                if (sender.hasPermission(str)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
