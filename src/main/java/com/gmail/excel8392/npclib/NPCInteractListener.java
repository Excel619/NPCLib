package com.gmail.excel8392.npclib;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class NPCInteractListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (NPCLib.getNPCEntities().containsKey(((CraftPlayer) event.getEntity()).getHandle())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (event.getRightClicked().getType() == EntityType.PLAYER) {
                if (NPCLib.getNPCEntities().containsKey(((CraftPlayer) event.getRightClicked()).getHandle())) {
                    Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(NPCLib.getNPCEntities().get(((CraftPlayer) event.getRightClicked()).getHandle()), event.getPlayer(), NPCInteractType.RIGHT_CLICK));
                }
            }
        }
    }
    @EventHandler
    public void onPunch(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (NPCLib.getNPCEntities().containsKey(((CraftPlayer) event.getEntity()).getHandle())) {
                Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(NPCLib.getNPCEntities().get(((CraftPlayer) event.getEntity()).getHandle()), (Player) event.getEntity(), NPCInteractType.LEFT_CLICK));
            }
        }
    }

}
