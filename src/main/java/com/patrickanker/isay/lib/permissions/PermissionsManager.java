package com.patrickanker.isay.lib.permissions;

import com.patrickanker.isay.lib.logging.ConsoleLogger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


public class PermissionsManager {
    private final Plugin owningPlugin;
    private static PermissionsManager instance;
    private static Permission handler;

    public PermissionsManager(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
        instance = this;
    }
    
    public void initialize() 
    {
        try {
            RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            handler = rsp.getProvider();
        } catch (Throwable t) {
            
        }
        
        if (handler != null) {
            ConsoleLogger.getLogger(owningPlugin.getName()).log("Captured Vault permissions");
        } else {
            ConsoleLogger.getLogger(owningPlugin.getName()).log("Defaulting to Bukkit permissions");
        }
    }

    public static boolean hasPermission(String name, String permission) {
        if (handler != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                return handler.has(p, permission);
            } else {
                return handler.has((World) null, name, permission);
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                return p.hasPermission(permission);
            } else {
                return false;
            }
        }
    }
    
    public static boolean hasPermission(String world, String name, String permission) {
        if (handler != null) {
            World w = Bukkit.getWorld(name);
            return handler.has(w, name, permission);
            
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                return p.hasPermission(permission);
            } else {
                return false;
            }
        }
    }
    
    public static void givePermission(String world, String name, String permission) {
        if (handler != null) {
            World w = Bukkit.getWorld(world);
            handler.playerAdd(w, name, permission);
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                PermissionAttachment attach = p.addAttachment(instance.owningPlugin);
                attach.setPermission(permission, true);
            } else {
                // Damn.
            }
        }
    }
    
    public static void givePermission(String name, String permission) {
        if (handler != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                handler.playerAdd(p, permission);
            } else {
                handler.playerAdd((World) null, name, permission);
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                PermissionAttachment attach = p.addAttachment(instance.owningPlugin);
                attach.setPermission(permission, true);
            } else {
                // Damn.
            }
        }
    }
    
    public static void removePermission(String world, String name, String permission) {
        if (handler != null) {
            World w = Bukkit.getWorld(world);
            handler.playerRemove(w, name, permission);
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                PermissionAttachment attach = p.addAttachment(instance.owningPlugin);
                attach.setPermission(permission, false);
            } else {
                // Damn.
            }
        }
    }
    
    public void removePermission(String name, String permission) {
        if (handler != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                handler.playerRemove(p, permission);
            } else {
                handler.playerRemove((World) null, name, permission);
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                PermissionAttachment attach = p.addAttachment(instance.owningPlugin);
                attach.setPermission(permission, false);
            } else {
                // Damn.
            }
        }
    }
    
    public boolean inGroup(String name, String group) {
        if (handler != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                return handler.playerInGroup(p, group);
            } else {
                return handler.playerInGroup((World) null, name, group);
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                return p.hasPermission("group." + group);
            } else {
                return false;
            }
        }
    }
    
    public static List<String> getGroups(String name) {
        List<String> groups = new LinkedList<String>();
        
        if (handler != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                groups.addAll(Arrays.asList(handler.getPlayerGroups(p)));
            } else {
                groups.addAll(Arrays.asList(handler.getPlayerGroups((World) null, name)));
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Player p = op.getPlayer();
            
            if (p != null) {
                PermissionAttachment attach = p.addAttachment(instance.owningPlugin);
                Map<String, Boolean> permMap = attach.getPermissions();
                
                for (Map.Entry<String, Boolean> entry : permMap.entrySet()) {
                    String perm = entry.getKey();
                    boolean value = entry.getValue();
                    
                    if (perm.matches("^(group)\\.([a-zA-Z])+") && value) {
                        String group = perm.replace("group.", "");
                        groups.add(group);
                    }
                }
            }
        }
        
        return groups;
    }
    
//    public void addGroup(String username, String groupname) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    public void removeGroup(String username, String groupname) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    public void giveGroupPermission(String world, String name, String permission) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    public void giveGroupPermission(String name, String permission) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    public void removeGroupPermission(String world, String name, String permission) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    public void removeGroupPermission(String name, String permission) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
