package com.patrickanker.isay;

import com.patrickanker.isay.core.ChatPlayer;
import com.patrickanker.isay.core.MessagePreprocessingHandler;
import com.patrickanker.isay.messageprocessing.PingManager;
import com.patrickanker.isay.messageprocessing.ItemAliasManager;
import com.patrickanker.isay.core.channels.ChannelManager;
import com.patrickanker.isay.commands.*;
import com.patrickanker.isay.lib.commands.CommandManager;
import com.patrickanker.isay.lib.commands.CommandOverrideHelper;
import com.patrickanker.isay.lib.config.PropertyConfiguration;
import com.patrickanker.isay.lib.logging.ConsoleLogger;
import com.patrickanker.isay.lib.logging.FileLogger;
import com.patrickanker.isay.lib.permissions.PermissionsManager;
import com.patrickanker.isay.core.Formatter;
import com.patrickanker.isay.listeners.PlayerListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public final class ISMain extends JavaPlugin {

    private static ISMain instance;
    private PermissionsManager permsManager;
    private CommandManager commandManager;
    private GroupManager groupManager;
    private ChannelManager channelManager;
    private MessagePreprocessingHandler messagePreprocessingHandler;
    private CommandOverrideHelper commandOverrideHelper;
    private final PropertyConfiguration config = new PropertyConfiguration("/iSay/iSay");
    private final YamlConfiguration playerGroupConfig = new YamlConfiguration();
    private final YamlConfiguration channelConfig = new YamlConfiguration();
    private final List<ChatPlayer> registeredPlayers = new LinkedList<ChatPlayer>();
    public final List<String> muteSleepPlayers = new LinkedList<String>(); // Yes, I know unethical. Dirty fix.
    
    private static final String defaultMessageFormat = "$id $m";
    private static final String defaultBroadcastFormat = "&f[&cBroadcast&f] &a$m";
    private static final String defaultConsoleFormat = "&d[Server] $m";

    @Override
    public void onDisable()
    {
        messagePreprocessingHandler.terminateProcesses();
        channelManager.shutDown();
        
        for (ChatPlayer cp : registeredPlayers) {
            cp.save();
        }
        
        unregisterAllPlayers();

        try {
            playerGroupConfig.save(new File("plugins/iSay/players.yml"));
        } catch (IOException ex) {
            log("Could not save player data file", 2);
        }

        groupManager.saveGroupConfigurations();
        config.save();
    }

    @Override
    public void onEnable()
    {   
        headerDebug();
        
        instance = this;
        config.load();
        debugLog("Configuration loaded");

        if ((getConfigData().getString("reset") == null) || (getConfigData().getString("reset").equalsIgnoreCase("yes"))) {
            loadFactorySettings();
            debugLog("Configuration defaulted");
        }
        
        commandManager = new CommandManager(this);
        commandOverrideHelper = new CommandOverrideHelper();
        permsManager = new PermissionsManager(this);
        groupManager = new GroupManager();
        channelManager = new ChannelManager();
        messagePreprocessingHandler = new MessagePreprocessingHandler();
        debugLog("Managers instantiated");
        
        groupManager.load();
        debugLog("Group manager: Done");
        
        commandManager.registerCommands(ChannelCommands.class);
        commandManager.registerCommands(GeneralCommands.class);
        commandManager.registerCommands(MessagingCommands.class);
        commandManager.registerCommands(AdministrativeCommands.class);
        commandManager.registerCommands(ModerationCommands.class);
        commandManager.registerCommands(PlayerCommands.class);
        debugLog("Command manager: Done");
        
        messagePreprocessingHandler.registerProcess(ItemAliasManager.class, MessagePreprocessingHandler.IMPORTANCE.NORMAL);
        messagePreprocessingHandler.registerProcess(PingManager.class, MessagePreprocessingHandler.IMPORTANCE.NORMAL);
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        permsManager.initialize();
        
        try {
            File _players = new File("plugins/iSay/players.yml");
            
            if (!_players.exists()) {
                _players.getParentFile().mkdirs();
                _players.createNewFile();
            }
            
            playerGroupConfig.load(_players);
        } catch (FileNotFoundException ex) {
            log("Could not load player data", 2);
            debugLog("[ERROR] Could not load player data: " + ex.getMessage());
        } catch (IOException ex) {
            log("Could not load player data", 2);
            debugLog("[ERROR] Could not load player data: " + ex.getMessage());
        } catch (InvalidConfigurationException ex) {
            log("Could not load player data", 2);
            debugLog("[ERROR] Could not load player data: " + ex.getMessage());
        }
        
        debugLog("Permissions manager: Done");

        for (Player p : Bukkit.getOnlinePlayers()) {
            ChatPlayer foo = registerPlayer(p);
            channelManager.onPlayerLogin(foo);
        }
        
        // Init Metrics
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            
            if (!metrics.isOptOut()) {
                metrics.start();
                debugLog("Metrics: Enabled");
            } else {
                debugLog("Metrics: Disabled");
            }
        } catch (Throwable t) {
            ISMain.log("Could not send statistics to Metrics", 1);
            debugLog("[WARNING] Could not send statistics to Metrics: " + t.getMessage());
        }
        
        debugLog("iSay enabled");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args)
    {
        return commandManager.executeCommandProcessErrors(command, cs, args, this);
    }

    public ChannelManager getChannelManager()
    {
        return channelManager;
    }

    public PropertyConfiguration getConfigData()
    {
        return config;
    }

    public YamlConfiguration getPlayerConfig()
    {
        return playerGroupConfig;
    }
    
    public YamlConfiguration getChannelConfig()
    {
        return channelConfig;
    }

    public static ISMain getInstance()
    {
        return instance;
    }

    public GroupManager getGroupManager()
    {
        return groupManager;
    }
    
    public MessagePreprocessingHandler getMessagePreprocessHandler()
    {
        return messagePreprocessingHandler;
    }
    
    public CommandOverrideHelper getCommandOverrideHelper()
    {
        return commandOverrideHelper;
    }

    private ChatPlayer registerPlayer(Player player)
    {
        if (player != null) {
            ChatPlayer cp = new ChatPlayer(player);
            registerPlayer(cp);
            return cp;
        } else {
            return null;
        }
    }

    private void registerPlayer(ChatPlayer cp)
    {
        if (!registeredPlayers.contains(cp)) {
            registeredPlayers.add(cp);
        }
    }

    public void unregisterPlayer(Player player)
    {
        if (isPlayerRegistered(player)) {
            ChatPlayer cp = getRegisteredPlayer(player);
            unregisterPlayer(cp);
        }
    }

    private void unregisterPlayer(ChatPlayer cp)
    {
        if (registeredPlayers.contains(cp)) {
            registeredPlayers.remove(cp);
        }
    }

    private void unregisterAllPlayers()
    {
        registeredPlayers.clear();
    }

    public boolean isPlayerRegistered(Player player)
    {
        for (ChatPlayer cp : registeredPlayers) {
            if (cp.getPlayer().getName().equals(player.getName())) {
                return true;
            }
        }
        
        return false;
    }

    public ChatPlayer getRegisteredPlayer(Player player)
    {
        ChatPlayer ret = null;
        
        for (ChatPlayer cp : registeredPlayers) {
            if (cp.getPlayer().getName().equals(player.getName())) {
                ret = cp;
            }
        }
        
        if (ret != null) {
            return ret;
        } else {
            ChatPlayer cp = new ChatPlayer(player);
            registerPlayer(cp);
            return cp;
        }
    }

    public static String getDefaultBroadcastFormat()
    {
        return defaultBroadcastFormat;
    }

    public static String getDefaultConsoleFormat()
    {
        return defaultConsoleFormat;
    }

    public static String getDefaultMessageFormat()
    {
        return defaultMessageFormat;
    }

    private void loadFactorySettings()
    {
        getConfigData().setString("broadcast-format", "&f[&cBroadcast&f] &a$m");
        getConfigData().setString("console-format", "&d[Server] $m");
        getConfigData().setString("message-format", "$id $m");
        getConfigData().setBoolean("override-other-commands", true);
        getConfigData().setString("mute-key-phrase", "I agree. Allow me to chat.");

        getConfigData().setString("reset", "no");
        log("| ========================================== |");
        log("| * iSay                                     |");
        log("| *                                          |");
        log("| * Continue, good sir. I'm listening...     |");
        log("| *                                          |");
        log("| * Built by: psanker                        |");
        log("| * Licensed by the BSD License - 2012       |");
        log("| ========================================== |");
        log("Factory settings loaded");
    }

    public static void log(String str)
    {
        log(str, 0);
    }

    public static void log(String str, int importance)
    {
        str = Formatter.stripColors(str);

        String playerCopy = str;

        if (importance == 1) {
            playerCopy = "§6[WARNING] " + playerCopy;
        } else if (importance == 2) {
            playerCopy = "§c[ERROR] " + playerCopy;
        }

        if (ISMain.getInstance().channelManager != null) { // Post-plugin enable
            ISMain.getInstance().channelManager.getDebugChannel().dispatch(null, playerCopy);
        }
        
        ConsoleLogger.getLogger("iSay").log(str, importance);
    }
    
    public static void debugLog(String str)
    {
        FileLogger debugLogger = new FileLogger(new File("plugins/iSay/logs/debug.log"));
        debugLogger.log(str);
    }
    
    public static void debugLog(String[] strs)
    {
        for (String str : strs) {
            debugLog(str);
        }
    }
    
    private static void headerDebug()
    {
        debugLog("=======================================================");
        debugLog("COMMENCING ISAY SESSION                                ");
        debugLog("                                                       ");
        debugLog("=======================================================");
    }
}