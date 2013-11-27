package com.patrickanker.isay.channels;

import com.patrickanker.isay.core.channels.Channel;
import com.patrickanker.isay.core.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.MessageFormattingServices;
import com.patrickanker.isay.Statistician;
import com.patrickanker.isay.formatters.GhostMessageFormatter;
import com.patrickanker.isay.formatters.MessageFormatter;
import com.patrickanker.isay.util.permissions.PermissionsManager;
import com.patrickanker.isay.core.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class ChatChannel extends Channel {

    protected boolean def = false;
    protected boolean enabled = true;
    protected boolean helpop = false;
    protected boolean locked = false;
    protected boolean promoted = false;
    protected boolean verbose = true;
    protected String ghostformat = "&8[&f" + this.name + "&8] $group&f $message";
    protected String password = "";
    protected List<String> banlist = new LinkedList<String>();
    
    public static final String STATS_CURRENT_MESSAGE_COUNT = "chatchannel-message-current-message-count";
    public static final String STATS_MPM = "chatchannel-message-mps";

    public ChatChannel(String name)
    {
        super(name);
        load();
    }

    @Override
    public void connect(String player)
    {
        if (!hasListener(player)) {
            addListener(player, true);

            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()) && verbose) {
                    pl.sendMessage(ChatColor.GREEN + player + ChatColor.GRAY + " has joined " + ChatColor.GREEN + this.name + ChatColor.DARK_GRAY + ".");
                }
            }
        }
    }

    public void silentConnect(String player)
    {
        if (!hasListener(player)) {
            addListener(player, true);
        } else if ((hasListener(player)) && (!hasFocus(player))) {
            assignFocus(player, true);
        }
    }

    @Override
    public void dispatch(ChatPlayer cp, String message)
    {
        List<String> oldListeners = new LinkedList<String>();
        
        String copy = message;

        if (promoted && !PermissionsManager.hasPermission(cp.getPlayer().getName(), "isay.channels." + name + ".promoted")) {
            cp.sendMessage("§cThis is a promoted channel. You cannot chat without permission.");
            return;
        }
        
        if (MessageFormattingServices.containsURLs(message)) {
            copy = MessageFormattingServices.shortenURLs(message);
        }
        
        String focus = Formatter.selectFormatter(MessageFormatter.class).formatMessage(copy, cp);
        String ghost = Formatter.selectFormatter(GhostMessageFormatter.class).formatMessage(copy, cp, this);
        
        if (hasFocus(cp.getPlayer().getName())) {
            cp.sendMessage(focus);
        } else {
            cp.sendMessage(ghost);
        }

        for (Map.Entry l : this.listeners.entrySet()) {
            if (l.getKey().equals(cp.getPlayer().getName()))
                continue;
            
            OfflinePlayer op = Bukkit.getOfflinePlayer((String) l.getKey());
            
            if (op.getPlayer() == null) {
                oldListeners.add(op.getName());
                continue;
            }
            
            Player pl = op.getPlayer();
            ChatPlayer _cp = ISMain.getInstance().getRegisteredPlayer(pl);

            if (ISMain.getInstance().getChannelManager().getDebugChannel().hasListener(_cp.getPlayer().getName())) {
                continue;
            }

            if ((_cp.isIgnoring(cp)) || ((_cp.channelsMuted()) && (!isHelpOp()))) {
                continue;
            }

            if (((Boolean) l.getValue())) {
                _cp.sendMessage(focus);
                
            } else {
                _cp.sendMessage(ghost);
            }
        }
        
        for (String listener : oldListeners) {
            removeListener(listener);
        }
        
        // -- Stats --
        
        Statistician stats = Statistician.getStats();
        
        int count = stats.fetchInt(STATS_CURRENT_MESSAGE_COUNT);
        count += 1;
        
        if (count == 0) {
            count = 1;
        }
        
        stats.updateInt(STATS_CURRENT_MESSAGE_COUNT, count);

        ISMain.log(getName() + "-> " + cp.getPlayer().getName() + ": " + message);
    }

    @Override
    public void disconnect(String player)
    {
        if (hasListener(player)) {
            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()) && verbose) {
                    pl.sendMessage(ChatColor.GREEN + player + ChatColor.GRAY + " has left " + ChatColor.GREEN + this.name + ChatColor.DARK_GRAY + ".");
                }
            }

            removeListener(player);
        }
    }

    public void silentDisconnect(String player)
    {
        if (hasListener(player)) {
            removeListener(player);
        }
    }

    public List<String> getListenerList()
    {
        List<String> l = new LinkedList<String>();

        for (Map.Entry<String, Boolean> entry : listeners.entrySet()) {
            if (!l.contains(entry.getKey()))
                l.add(entry.getKey());
        }

        return l;
    }

    public HashMap<String, Boolean> getListenerMap()
    {
        return listeners;
    }

    @Override
    public void load()
    {   
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".default")) {
            this.def = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".default");
        }
        
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".enabled")) {
            this.enabled = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".enabled");
        }
        
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".helpop")) {
            this.helpop = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".helpop");
        }
        
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".locked")) {
            this.locked = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".locked");
        }
        
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".verbose")) {
            this.verbose = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".verbose");
        }

        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".promoted")) {
            this.promoted = ISMain.getInstance().getChannelConfig().getBoolean(this.name + ".promoted");
        }
        
        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".ghostformat")) {
            this.ghostformat = ISMain.getInstance().getChannelConfig().getString(this.name + ".ghostformat");
        }

        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".password")) {
            this.password = ISMain.getInstance().getChannelConfig().getString(this.name + ".password");
        }

        if (ISMain.getInstance().getChannelConfig().contains(this.name + ".banlist")) {
            this.banlist = ISMain.getInstance().getChannelConfig().getStringList(this.name + ".banlist");
        }
    }

    @Override
    public void dump()
    {
        ISMain.getInstance().getChannelConfig().set(this.name + ".default", this.def);
        ISMain.getInstance().getChannelConfig().set(this.name + ".enabled", this.enabled);
        ISMain.getInstance().getChannelConfig().set(this.name + ".helpop", this.helpop);
        ISMain.getInstance().getChannelConfig().set(this.name + ".locked", this.locked);
        ISMain.getInstance().getChannelConfig().set(this.name + ".verbose", this.verbose);
        ISMain.getInstance().getChannelConfig().set(this.name + ".promoted", this.promoted);
        ISMain.getInstance().getChannelConfig().set(this.name + ".ghostformat", this.ghostformat);
        ISMain.getInstance().getChannelConfig().set(this.name + ".password", this.password);
        ISMain.getInstance().getChannelConfig().set(this.name + ".banlist", this.banlist);
    }

    public void setDefault(boolean bool)
    {
        this.def = bool;
    }

    public void setEnabled(boolean bool)
    {
        this.enabled = bool;
    }

    public void setHelpOp(boolean bool)
    {
        this.helpop = bool;
    }

    public void setLocked(boolean bool)
    {
        this.locked = bool;
    }
    
    public void setVerbose(boolean bool)
    {
        this.verbose = bool;
    }

    public void setPromoted(boolean bool)
    {
        this.promoted = bool;
    }

    public void setGhostFormat(String str)
    {
        this.ghostformat = str;
    }

    public void setPassword(String str)
    {
        this.password = str;
    }

    public void addBannedListener(String str)
    {
        if (!banlist.contains(str))
            banlist.add(str);
    }

    public void removeBannedListener(String str)
    {
        if (banlist.contains(str))
            banlist.remove(str);
    }

    public boolean isDefault()
    {
        return this.def;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public boolean isHelpOp()
    {
        return this.helpop;
    }

    public boolean isLocked()
    {
        return this.locked;
    }
    
    public boolean isVerbose()
    {
        return this.verbose;
    }

    public boolean isPromoted()
    {
        return this.promoted;
    }

    public boolean isBanned(String str)
    {
        return banlist.contains(str);
    }

    public String getGhostFormat()
    {
        return this.ghostformat;
    }

    public String getPassword()
    {
        return this.password;
    }
}