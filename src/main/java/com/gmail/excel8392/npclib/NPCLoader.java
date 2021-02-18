package com.gmail.excel8392.npclib;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NPCLoader {

    public static void loadNPCs(FileConfiguration config) {
        Map<Integer, NPC> npcs = new HashMap<>();
        if (config.contains("npcs")) {
            ConfigurationSection npcsSection = config.getConfigurationSection("npcs");
            for (String key : npcsSection.getKeys(false)) {
                String uuid = npcsSection.getString(key + ".uuid");
                npcs.put(Integer.parseInt(key), new NPC(
                        new Location(
                                Bukkit.getWorld(npcsSection.getString(key + ".location.world")),
                                Double.parseDouble(npcsSection.getString(key + ".location.x")),
                                Double.parseDouble(npcsSection.getString(key + ".location.y")),
                                Double.parseDouble(npcsSection.getString(key + ".location.z")),
                                Float.parseFloat(npcsSection.getString(key + ".location.yaw")),
                                Float.parseFloat(npcsSection.getString(key + ".location.pitch"))),
                        new NPCSkin(npcsSection.getString(key + ".skin-texture"), npcsSection.getString(key + ".skin-signature")),
                        Integer.parseInt(key),
                        uuid,
                        npcsSection.getString(key + ".name"),
                        npcsSection.getBoolean(key + ".name-shown"),
                        npcsSection.getBoolean(key + ".shown"),
                        true));
            }
        }
        NPCLib.setNPCs(npcs);
        NPCLib.setNPCEntities(sortNPCsByEntity(npcs));
        NPCHandler.placeNPCsInGrid(npcs);
        Bukkit.getLogger().log(Level.INFO, "[NPCLib] NPCs have been loaded!");
    }

    private static Map<EntityPlayer, NPC> sortNPCsByEntity(Map<Integer, NPC> npcs) {
        Map<EntityPlayer, NPC> npcEntityIds = new HashMap<>();
        for (Map.Entry<Integer, NPC> entry : npcs.entrySet()) {
            npcEntityIds.put(entry.getValue().getEntityPlayer(), entry.getValue());
        }
        return npcEntityIds;
    }

    public static void saveNPC(NPC npc, FileConfiguration config) {
        Bukkit.getScheduler().runTaskAsynchronously(NPCLib.getInstance(), () -> {
            config.set("npcs." + npc.getId() + ".location.world", npc.getLocation().getWorld().getName());
            config.set("npcs." + npc.getId() + ".location.x", npc.getLocation().getX());
            config.set("npcs." + npc.getId() + ".location.y", npc.getLocation().getY());
            config.set("npcs." + npc.getId() + ".location.z", npc.getLocation().getZ());
            config.set("npcs." + npc.getId() + ".location.yaw", npc.getLocation().getYaw());
            config.set("npcs." + npc.getId() + ".location.pitch", npc.getLocation().getPitch());
            config.set("npcs." + npc.getId() + ".skin-texture", npc.getSkin().getTexture());
            config.set("npcs." + npc.getId() + ".skin-signature", npc.getSkin().getSignature());
            config.set("npcs." + npc.getId() + ".uuid", npc.getUuid());
            config.set("npcs." + npc.getId() + ".name", npc.getName());
            config.set("npcs." + npc.getId() + ".name-shown", npc.shouldShowName());
            config.set("npcs." + npc.getId() + ".shown", npc.isShown());
            try {
                config.save(new File(NPCLib.getInstance().getDataFolder(), "npcs.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void deleteNPC(Integer id, FileConfiguration config) {
        Bukkit.getScheduler().runTaskAsynchronously(NPCLib.getInstance(), () -> config.set("npcs." + id, null));
    }

    public static Integer loadNextId(FileConfiguration config) {
        if (config.contains("next-id")) {
            return config.getInt("next-id");
        } else {
            config.set("next-id", 0);
            try {
                config.save(new File(NPCLib.getInstance().getDataFolder(), "npcs.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

}
