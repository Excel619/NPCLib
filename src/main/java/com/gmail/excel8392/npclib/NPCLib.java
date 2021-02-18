package com.gmail.excel8392.npclib;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class NPCLib extends JavaPlugin {

    private static NPCLib instance;

    private static Map<Integer, NPC> npcs = new HashMap<>();
    private static Map<EntityPlayer, NPC> npcEntities = new HashMap<>();
    private static FileConfiguration config;

    private static final Map<UUID, NPCSkin> cachedSkins = new HashMap<>();
    private static boolean shouldCacheSkins = false;

    private static Integer nextId;

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new NPCInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new NPCHandler(), this);
        Bukkit.getPluginManager().registerEvents(new NPCScoreboardHandler(), this);
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdir()) {
                Bukkit.getLogger().log(Level.WARNING, "[NPCLib] WARNING: Failed to create plugin directory, server jar missing permissions! Aborting startup...");
                return;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            config = getYamlConfigFile("npcs.yml", instance.getDataFolder());
            nextId = NPCLoader.loadNextId(config);
            Bukkit.getScheduler().runTask(instance, () -> {
                NPCLoader.loadNPCs(config);
                NPCScoreboardHandler.initScoreboard();
                Bukkit.getScheduler().scheduleAsyncRepeatingTask(NPCLib.this, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (NPCHandler.hasLoadedDataForPlayer(player)) {
                            NPCHandler.updateNPCsForPlayer(player);
                        }
                    }
                }, 7 * 20, 7 * 20);
            });
        });
    }

    public static FileConfiguration getYamlConfigFile(String fileName, File folder) {
        FileConfiguration config;
        File file;
        file = new File(folder, fileName);
        config = new YamlConfiguration();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            config.load(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return config;
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Integer, NPC> npc : npcs.entrySet()) {
            npc.getValue().delete();
        }
    }

    public static void updateNPCs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NPCHandler.updateNPCsForPlayer(player);
        }
    }

    public static Map<Integer, NPC> getNPCs() {
        return npcs;
    }

    public static Map<EntityPlayer, NPC> getNPCEntities() {
        return npcEntities;
    }

    public static Map<UUID, NPCSkin> getCachedSkins() {
        return cachedSkins;
    }

    public static boolean shouldCacheSkins() {
        return shouldCacheSkins;
    }

    public static Integer getNextId() {
        final Integer current = new Integer(nextId);
        final Integer next = new Integer(++nextId);
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> getFileConfig().set("next-id", next));
        return current;
    }

    public static FileConfiguration getFileConfig() {
        return config;
    }

    public static void setNPCs(Map<Integer, NPC> npcs) {
        NPCLib.npcs = npcs;
    }

    public static void setNPCEntities(Map<EntityPlayer, NPC> npcEntities) {
        NPCLib.npcEntities = npcEntities;
    }

    public static void setShouldCacheSkins(boolean shouldCacheSkins) {
        NPCLib.shouldCacheSkins = shouldCacheSkins;
    }

    public static NPCLib getInstance() {
        return instance;
    }

}
