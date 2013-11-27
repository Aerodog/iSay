package com.patrickanker.isay.commands;

import com.patrickanker.isay.core.ChatPlayer;
import com.patrickanker.isay.*;
import com.patrickanker.isay.channels.DebugChannel;
import com.patrickanker.isay.core.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.isay.util.commands.Command;
import com.patrickanker.isay.util.commands.CommandPermission;

import java.util.*;
import org.bukkit.Bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AdministrativeCommands {
    
    private static final String ADMIN_PERMISSION = "isay.admin";
    private static final String LOGO = "§b§oi§rSay";
    
    @Command(aliases={"isay"},
            bounds={1, -1},
            help="§c/isay debug §ftoggles iSay Debug mode\n"
            + "§c/isay reload §freloads iSay from config\n"
            + "§c/isay save §fsaves iSay to config\n"
            + "§c/isay info §fshows general information of iSay\n"
            + "§c/isay info -c <channel> §fshows info about a specific channel\n"
            + "§c/isay info -c <channel> [-l, listeners] §fshows the listeners of a channel\n"
            + "§c/isay info -p <player> §fshows info about a specific player")
    @CommandPermission(ADMIN_PERMISSION)
    public void isayCommand(CommandSender cs, final String[] args) 
    {
        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            cs.sendMessage(LOGO + " §7reloaded.");
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length == 1) {
                info(cs, null);
            } else if (args.length == 3 && args[1].equalsIgnoreCase("-c")) {
                info(cs, args[2], false);
            } else if (args.length == 4 && args[1].equalsIgnoreCase("-c") && (args[3].equalsIgnoreCase("-l") || args[3].equalsIgnoreCase("listeners"))) {
                showChannelListeners(cs, args[2]);
            } else if (args.length == 3 && args[1].equalsIgnoreCase("-p")) {
                info(cs, args[2], true);
            }
            
        } else if (args[0].equalsIgnoreCase("save")) {
            saveConfig();
            cs.sendMessage(LOGO + " §7saved.");
        } else if (args[0].equalsIgnoreCase("debug")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("§cThe console already views iSay in debug mode.");
                return;
            }
            
            if (args.length == 1 || !args[1].equals(ISMain.getInstance().getConfigData().getString("debug-channel-password"))) {
                cs.sendMessage("§cYou are not authorised to view the Debug Channel.");
                return;
            }

            DebugChannel debugChannel = ISMain.getInstance().getChannelManager().getDebugChannel();

            if (debugChannel.hasListener(cs.getName())) {
                debugChannel.removeListener(cs.getName());
                cs.sendMessage(LOGO + " §7debug mode deactivated.");
            } else {
                debugChannel.addListener(cs.getName());
                cs.sendMessage(LOGO + " §7debug mode activated.");
            }
        }
    }
    
    private void reloadConfig() 
    {
        ISMain.getInstance().getChannelManager().reloadChannels();
        
        try {
            ISMain.getInstance().getPlayerConfig().load("plugins/iSay/players.yml");
        } catch (Throwable t) {
            ISMain.log("Could not reload player config", 2);
        }
        
        ISMain.getInstance().getGroupManager().load();
        ISMain.getInstance().getConfigData().load();
    }
    
    private void saveConfig()
    {
        ISMain.getInstance().getChannelManager().saveChannels();
        
        try {
            ISMain.getInstance().getPlayerConfig().save("plugins/iSay/players.yml");
        } catch (Throwable t) {
            ISMain.log("Could not reload player config", 2);
        }
        
        ISMain.getInstance().getGroupManager().saveGroupConfigurations();
        ISMain.getInstance().getConfigData().save();
    }
    
    private void info(CommandSender cs, String name)
    {
        info(cs, name, false);
    }
    
    private void info(CommandSender cs, String name, boolean player)
    {
        if (name == null) {
            cs.sendMessage("§8====================");
            cs.sendMessage(LOGO + " §7version §a" + ISMain.getInstance().getDescription().getVersion());
            cs.sendMessage("§8");
            cs.sendMessage("§7Channels§f: §a" + ISMain.getInstance().getChannelManager().getList().size());
            
            int count = Statistician.getStats().fetchInt(ChatChannel.STATS_CURRENT_MESSAGE_COUNT);
            
            if (count == -1) {
                count = 0;
                Statistician.getStats().updateInt(ChatChannel.STATS_CURRENT_MESSAGE_COUNT, count);
            }
            
            cs.sendMessage("§7Total sent messages§f: §a" + Statistician.getStats().fetchInt(ChatChannel.STATS_CURRENT_MESSAGE_COUNT));
            
            int mpm = Statistician.getStats().fetchInt(ChatChannel.STATS_MPM);
            
            if (mpm == -1) {
                cs.sendMessage("§7MPM§f: §cNot yet calculated");
            } else {
                cs.sendMessage("§7MPM§f: §a" + mpm);
            }
            
            cs.sendMessage("§8====================");
        } else if (!player) {
            List<Channel> l = ISMain.getInstance().getChannelManager().matchChannel(name);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel channel = (ChatChannel) l.get(0);
                
                cs.sendMessage("§8====================");
                cs.sendMessage(LOGO + " §7channel §6" + channel.getName());
                cs.sendMessage("§8");
                cs.sendMessage("§7Default§f: §a" + channel.isDefault());
                cs.sendMessage("§7Enabled§f: §a" + channel.isEnabled());
                cs.sendMessage("§7HelpOp§f: §a" + channel.isHelpOp());
                cs.sendMessage("§7Locked§f: §a" + channel.isLocked());
                cs.sendMessage("§7Verbose§f: §a" + channel.isVerbose());
                cs.sendMessage("§7Ghost Format§f: §a" + channel.getGhostFormat());
                cs.sendMessage("§7Password§f: §a" + (channel.getPassword().equals("") ? "null" : channel.getPassword()));
                cs.sendMessage("§7Listeners§f: §a" + channel.getListenerList().size());
                
                int focused = 0;
                int ghosted = 0;
                
                for (Map.Entry<String, Boolean> entry : channel.getListenerMap().entrySet()) {
                    if (entry.getValue())
                        ++focused;
                    else
                        ++ghosted;
                }
                
                cs.sendMessage("§8- §7Focused§f: §a" + focused);
                cs.sendMessage("§8- §7Ghosted§f: §a" + ghosted);
                cs.sendMessage("§8");
                cs.sendMessage("§7View the list of listeners with §a/isay info -c " + channel.getName() + " listeners");
                cs.sendMessage("§8====================");
            }
        } else if (player) {
            List<Player> l = Bukkit.matchPlayer(name);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                Player p = l.get(0);
                ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(p);
                
                cs.sendMessage("§8====================");
                cs.sendMessage(LOGO + " §7player data for §6" + p.getName());
                cs.sendMessage("§8");
                cs.sendMessage("§7ID§f: §a" + cp.getFormat());
                cs.sendMessage("§8====================");
            }
        }
    }
    
    private void showChannelListeners(CommandSender cs, String channel)
    {
        List<Channel> l = ISMain.getInstance().getChannelManager().matchChannel(channel);
            
        if (l.isEmpty()) {
            cs.sendMessage("§cNo channel found with that name.");
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple channels found with that name.");
        } else {
            ChatChannel c = (ChatChannel) l.get(0);
            List<String> list = new LinkedList<String>();
            
            for (String listener : c.getListenerList()) {
                list.add(listener);
            }
            
            if (list.isEmpty()) {
                cs.sendMessage("§cThere are no listeners to this channel.");
                return;
            }

            Collections.sort(list);
            
            cs.sendMessage("§8====================");
            cs.sendMessage(LOGO + " §7channel §6" + c.getName());
            cs.sendMessage("§8-- §6Listeners");
            cs.sendMessage("§8");
            
            int i = 1;
            
            for (String str : list) {
                cs.sendMessage("§f" + i + "§8.) " + ((c.hasFocus(str)) ? "§a" : "§7") + str);
                ++i;
            }
            
            cs.sendMessage("§8====================");
        }
    }
}
