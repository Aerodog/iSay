package com.patrickanker.isay.listeners;

import com.patrickanker.isay.core.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.core.MessagePreprocessingHandler;
import com.patrickanker.isay.MuteServices;
import com.patrickanker.isay.core.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        // WorldEdit CUI call
        if (event.getMessage().startsWith("u00a7")) {
            event.setCancelled(true);
            return;
        }
        
        ISMain.getInstance().getMessagePreprocessHandler().process(event.getPlayer(), event.getMessage());
        
        if (ISMain.getInstance().getMessagePreprocessHandler().getStatus() == MessagePreprocessingHandler.STATUS.TERMINATED) {
            event.getRecipients().clear();
            event.setCancelled(true);
            ISMain.getInstance().getMessagePreprocessHandler().resetStatus();
            return;
        }

        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());
        
        if (cp.isMuted()) {
            if (!ISMain.getInstance().getConfigData().getString("mute-key-phrase").equals(event.getMessage())) {
                MuteServices.muteWarn(cp);
            } else {
                if (!cp.muteTimedOut()) {
                    MuteServices.muteWarn(cp);
                    event.setCancelled(true);
                    return;
                }
                
                cp.setMuted(false);
                cp.setMuteTimeout("");
                MuteServices.unmuteAnnounce(cp);
            }

            event.getRecipients().clear();
            event.setCancelled(true);
            return;
        }

        Channel channel = ISMain.getInstance().getChannelManager().getFocus(event.getPlayer().getName());

        if (channel != null) {
            ChatChannel cc = (ChatChannel) channel;
            cc.dispatch(cp, event.getMessage());

            List<Player> bukkitDispatch = new LinkedList<Player>();
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (cc.hasFocus(p.getName()))
                    bukkitDispatch.add(p);
                    
            }
            
            event.getRecipients().clear();
            event.getRecipients().addAll(bukkitDispatch);

            event.setCancelled(true);
        } else {
            event.getPlayer().sendMessage("§cYou do not have a channel focus.");
            event.setCancelled(true);
            event.getRecipients().clear();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        ISMain.getInstance().getGroupManager().verifyPlayerGroupExistence(event.getPlayer());

        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());
        ISMain.getInstance().getChannelManager().onPlayerLogin(cp);
        
        if (ISMain.getInstance().muteSleepPlayers.contains(cp.getPlayer().getName())) {
            ISMain.getInstance().muteSleepPlayers.remove(cp.getPlayer().getName());
            cp.setMuted(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());

        ISMain.getInstance().getChannelManager().onPlayerLogoff(cp);
        ISMain.getInstance().unregisterPlayer(event.getPlayer());
        
        if (cp.isMuted())
            ISMain.getInstance().muteSleepPlayers.add(cp.getPlayer().getName());
        
        cp.save();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());

        ISMain.getInstance().getChannelManager().onPlayerLogoff(cp);
        ISMain.getInstance().unregisterPlayer(event.getPlayer());
        
        if (cp.isMuted())
            ISMain.getInstance().muteSleepPlayers.add(cp.getPlayer().getName());
        
        cp.save();
    }
}
