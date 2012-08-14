package com.thevoxelbox.isay.formatters;

import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;
import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;

public class MessageFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs)
    {
        ChatPlayer cp = (ChatPlayer) otherArgs[0];
        
        String master = ISMain.getConfigData().getString("message-format");

        if (master == null) {
            master = ISMain.getDefaultMessageFormat();
            ISMain.getConfigData().setString("message-format", ISMain.getDefaultMessageFormat());
        }

        String idFormat = cp.getFormat();
        String groupIdFormat = cp.getGroupFormat();
        
        if (groupIdFormat == null) {
            groupIdFormat = "$name:";
        }

        idFormat = idFormat.replace("$name", cp.getPlayer().getName());
        idFormat = idFormat.replace("$n", cp.getPlayer().getName());
        idFormat = Formatter.encodeColors(idFormat);

        groupIdFormat = groupIdFormat.replace("$name", cp.getPlayer().getName());
        groupIdFormat = groupIdFormat.replace("$n", cp.getPlayer().getName());
        groupIdFormat = Formatter.encodeColors(groupIdFormat);

        master = master.replace("$id", idFormat);
        master = master.replace("$group", groupIdFormat);
        master = master.replace("$g", groupIdFormat);

        if (PermissionsManager.getHandler().hasPermission(cp.getPlayer().getWorld().getName(), cp.getPlayer().getName(), "isay.chat.color")) {
            in = Formatter.encodeColors(in);
        }
        master = master.replace("&", "§");
        master = master.replace("$name", cp.getPlayer().getName());
        master = master.replace("$n", cp.getPlayer().getName());

        master = master.replace("$message", in);
        master = master.replace("$m", in);
        return master;
    }

    @Override
    public String[] formatMessages(String in, Object... otherArgs)
    {
        return new String[]{formatMessage(in, otherArgs)};
    }
}