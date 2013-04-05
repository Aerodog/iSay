package com.patrickanker.isay.listeners;

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.MuteServices;
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        // WorldEdit CUI call
        if (event.getMessage().startsWith("u00a7")) {
            event.setCancelled(true);
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

            Set<Player> recipients = event.getRecipients();
            recipients.clear();

            event.setCancelled(true);
            return;
        }

        if (event.getPlayer().getItemInHand() != null) {
            String prefix = ISMain.getInstance().getItemAliasManager().getAliasForItem(event.getPlayer().getItemInHand().getTypeId());

            if (prefix != null) {
                event.getPlayer().chat(prefix + " " + event.getMessage());
                event.setCancelled(true);
                return;
            }
        }

        Channel channel = ISMain.getInstance().getChannelManager().getFocus(event.getPlayer().getName());

        if (channel != null) {
            ChatChannel cc = (ChatChannel) channel;
            cc.dispatch(cp, event.getMessage());

            Set<Player> recipients = event.getRecipients();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!cc.isHelpOp() && !cc.hasFocus(p.getName()))
                    recipients.remove(p);
                else if (cc.isHelpOp() && !cc.hasListener(p.getName()))
                    recipients.remove(p);
            }

            event.setCancelled(true);
        } else {
            event.getPlayer().sendMessage("§cYou do not have a channel focus.");
            event.setCancelled(true);

            Set<Player> recipients = event.getRecipients();
            recipients.clear();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        ISMain.getInstance().getGroupManager().verifyPlayerGroupExistence(event.getPlayer());

        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());
        ISMain.getInstance().getChannelManager().onPlayerLogin(cp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());

        ISMain.getInstance().getChannelManager().onPlayerLogoff(cp);
        ISMain.getInstance().unregisterPlayer(event.getPlayer());
        cp.save();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        ChatPlayer cp = ISMain.getInstance().getRegisteredPlayer(event.getPlayer());

        ISMain.getInstance().getChannelManager().onPlayerLogoff(cp);
        ISMain.getInstance().unregisterPlayer(event.getPlayer());
        cp.save();
    }
}