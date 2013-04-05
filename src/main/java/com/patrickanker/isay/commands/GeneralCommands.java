package com.patrickanker.isay.commands;

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.isay.formatters.MessageFormatter;
import com.patrickanker.isay.lib.commands.Command;
import com.patrickanker.isay.lib.commands.CommandPermission;
import com.patrickanker.isay.lib.commands.Subcommands;
import com.patrickanker.isay.lib.permissions.PermissionsManager;
import com.patrickanker.isay.lib.util.Formatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCommands {

    @Command(aliases = {"format", "f"}, 
            bounds = {2, -1},
            help = "§c/format -p <name> [format...] §fformats a player's name\n"
                 + "Common format anchors are §c$n§f and §c$g§f\n"
                 + "§c$n §ftakes the place of the player's name whereas\n"
                 + "§c$g §ftakes the place of the general group format for the player.\n"
                 + "§c/format -g <group> [format...] §fformats a group's format\n"
                 + "Only §c$g §fis used in this one.\n"
                 + "§c/format -cgf <channel> [format...] §fformats a channel's ghost format\n"
                 + "Common format anchors are §c$n§f, §c$g§f, and §c$m\n"
                 + "§c$n §ftakes the place of a player's name and\n"
                 + "§c$g §ftakes the place of the general group format for a player.\n"
                 + "§c$m §ftakes the place of the chat message\n"
                 + "§c$m must be present in the channel ghost format.")
    @Subcommands(arguments = {"-p", "-g", "-cgf"}, 
            permission = {"isay.general.format.players", "isay.general.format.groups", "isay.general.format.channels"})
    public void format(CommandSender cs, String[] args)
    {
        if (args[0].equalsIgnoreCase("-p")) {
            List l = Bukkit.matchPlayer(args[1]);

            if (l.isEmpty()) {
                if (ISMain.getInstance().getPlayerConfig().contains(args[1] + ".format")) {
                    String concat = "";
                    
                    for (int i = 2; i < args.length; i++) {
                        concat = concat + args[i] + " ";
                    }
                    
                    ISMain.getInstance().getPlayerConfig().set(args[1] + ".format", concat.trim());
                    
                    cs.sendMessage("§8==============================");
                    cs.sendMessage("Formatted §a" + args[1] + "§f's name to§8:");
                    cs.sendMessage(concat.trim());
                    cs.sendMessage("§6Preview of new format§8:");
                    cs.sendMessage("§cCannot render preview because player is not online.");
                    cs.sendMessage("§8==============================");
                    return;
                }
                
                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer((Player) l.get(0));
                String concat = "";

                for (int i = 2; i < args.length; i++) {
                    concat = concat + args[i] + " ";
                }

                cp.setFormat(concat.trim());

                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted §a" + cp.getPlayer().getName() + "§f's name to§8:");
                cs.sendMessage(cp.getFormat());
                cs.sendMessage("§6Preview of new format§8:");
                cs.sendMessage(Formatter.selectFormatter(MessageFormatter.class).formatMessage("Hello world.", cp));
                cs.sendMessage("§8==============================");
            }
        } else if (args[0].equalsIgnoreCase("-g")) {
            List<String> l = new ArrayList<String>();
            Iterator<String> it = ISMain.getInstance().getGroupManager().getRegisteredGroups().listIterator();

            while (it.hasNext()) {
                String group = (String) it.next();

                if (group.toLowerCase().startsWith(args[1].toLowerCase())) {
                    l.add(group);
                }
            }

            if (l.isEmpty()) {
                cs.sendMessage("§cNo group found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple groups found with that name.");
            } else {
                String concat = "";

                for (int i = 2; i < args.length; i++) {
                    concat = concat + args[i] + " ";
                }

                ISMain.getInstance().getGroupManager().getGroupConfiguration((String) l.get(0)).setString("format", concat.trim());

                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted the §a" + (String) l.get(0) + "§f group's name format to§8:");
                cs.sendMessage(concat.trim());
                cs.sendMessage("§8==============================");
            }
        } else if (args[0].equalsIgnoreCase("-cgf")) {
            List l = ISMain.getInstance().getChannelManager().matchChannel(args[1]);

            if (l.isEmpty()) {
                cs.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel cc = (ChatChannel) l.get(0);
                String concat = "";

                for (int i = 2; i < args.length; i++) {
                    concat = concat + args[i] + " ";
                }

                cc.setGhostFormat(concat.trim());

                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted the §a" + ((Channel) l.get(0)).getName() + "§f channel's name format to§8:");
                cs.sendMessage(cc.getGhostFormat());
                cs.sendMessage("§8==============================");
            }
        } else {
            cs.sendMessage("§cInvalid usage. See \"/format help\" for assitance.");
        }
    }
    
    @Command(aliases={"alias"},
            bounds={1, -1},
            help="§c/alias -p <player> <alias> §fsets the display name of <player> to <alias>")
    @CommandPermission("isay.general.alias")
    public void alias(CommandSender cs, String[] args) 
    {
        if (args.length == 3 && args[0].equalsIgnoreCase("-p")) {
            List<Player> l = Bukkit.matchPlayer(args[1]);
            
            if (l.isEmpty()) {
                if (ISMain.getInstance().getPlayerConfig().contains(args[1])) {
                    ISMain.getInstance().getPlayerConfig().set(args[1] + ".namealias", args[2]);
                    cs.sendMessage("§7Aliased §a" + args[1] + "§7's name to §a" + args[2]);
                    return;
                }

                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(l.get(0));
                cp.setNameAlias(args[2]);
                cs.sendMessage("§7Aliased §a" + cp.getPlayer().getName() + "§7's name to §a" + args[2]);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("-p")) {
            List<Player> l = Bukkit.matchPlayer(args[1]);
            
            if (l.isEmpty()) {
                if (ISMain.getInstance().getPlayerConfig().contains(args[1])) {
                    ISMain.getInstance().getPlayerConfig().set(args[1] + ".namealias", null);
                    cs.sendMessage("§7Reset §a" + args[1] + "§7's name");
                    return;
                }

                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(l.get(0));
                cp.setNameAlias(null);
                cs.sendMessage("§7Reset §a" + cp.getPlayer().getName() + "§7's name");
            }
        } else {
            cs.sendMessage("§cInvalid usage. See \"/alias help\" for assitance.");
        }
    }

    @Command(aliases = {"helpop", "/."}, 
            bounds = {1, -1}, 
            help = "Send a message to your server administrators with §c/helpop", 
            playerOnly = true)
    @CommandPermission("isay.general.helpop.send")
    public void helpop(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(p);

        if (!PermissionsManager.hasPermission(p.getName(), "isay.general.helpop.read")) {
            String concat = "";

            for (int i = 0; i < args.length; i++) {
                concat = concat + args[i] + " ";
            }

            ChatChannel helpop = (ChatChannel) ISMain.getInstance().getChannelManager().getHelpOpChannel();

            if (helpop == null) {
                p.sendMessage("§cHelpOp has not been configured on this server.");
            } else {
                helpop.dispatch(cp, concat.trim());
                p.sendMessage("§cYou have sent the following message to HelpOp:");
                p.sendMessage("§6" + concat.trim());
            }
        } else {
            ChatChannel helpop = (ChatChannel) ISMain.getInstance().getChannelManager().getHelpOpChannel();

            String concat = "";

            for (int i = 0; i < args.length; i++) {
                concat = concat + args[i] + " ";
            }

            if (helpop == null) {
                p.sendMessage("§cHelpOp has not been configured on this server.");
            } else {
                helpop.dispatch(cp, concat.trim());
            }
        }
    }
}