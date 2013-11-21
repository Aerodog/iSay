package com.patrickanker.isay.channels;

import com.patrickanker.isay.core.channels.Channel;
import com.patrickanker.isay.core.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DebugChannel extends Channel {

    private final String SESSION_UUID;

    public DebugChannel()
    {
        super("%%DEBUG%%");

        String sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String concat = "";
        Random rand = new Random();

        while (concat.length() < 16) {
            concat += sample.charAt(rand.nextInt(sample.length()));
        }

        SESSION_UUID = concat;
    }

    @Override
    public void connect(String player) {
        addListener(player);
    }

    @Override
    public void dispatch(ChatPlayer player, String message) {
        List<String> remove = new LinkedList<String>();

        for (Map.Entry<String, Boolean> listener : listeners.entrySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(listener.getKey());

            if (!op.isOnline()) {
                remove.add(listener.getKey());
            } else {
                Player p = op.getPlayer();

                p.sendMessage(message);
            }
        }
        
        for (String rem : remove) {
            listeners.remove(rem);
        }
    }

    @Override
    public void disconnect(String player) {
        removeListener(player);
    }

    @Override
    public void load() {
        // nope
    }

    @Override
    public void dump() {
        // nope
    }

    public String getSessionUUID()
    {
        return SESSION_UUID;
    }
}
