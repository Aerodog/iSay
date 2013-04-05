/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of Overcaffeinated Development nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
