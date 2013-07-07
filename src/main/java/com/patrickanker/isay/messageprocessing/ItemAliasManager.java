package com.patrickanker.isay.messageprocessing;

import com.patrickanker.isay.core.MessagePreprocessor;
import com.patrickanker.isay.lib.config.PropertyConfiguration;
import org.bukkit.entity.Player;

public class ItemAliasManager extends MessagePreprocessor {

    private final PropertyConfiguration itemConfig = new PropertyConfiguration("/iSay/items");

    public ItemAliasManager()
    {
        this.itemConfig.load();

        if (this.itemConfig.getAllEntries().isEmpty()) {
            this.itemConfig.setString("337", "/helpop");
            this.itemConfig.setString("318", "/broadcast");
        }
    }

    public String getAliasForItem(int type)
    {
        String foo = Integer.toString(type);

        if (this.itemConfig.hasEntry(foo)) {
            return this.itemConfig.getString(foo);
        }
        
        return null;
    }

    @Override
    public boolean process(Player sender, String message)
    {
        if (sender.getItemInHand() != null) {
            String prefix = getAliasForItem(sender.getItemInHand().getTypeId());

            if (prefix != null) {
                sender.chat(prefix + " " + message);
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void shutdown()
    {
        this.itemConfig.save();
    }
}