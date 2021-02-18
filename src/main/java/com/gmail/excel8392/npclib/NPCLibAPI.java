package com.gmail.excel8392.npclib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class NPCLibAPI {

    /**
     * Gets the NPCLib JavaPlugin.
     * @return NPCLib
     */
    public static NPCLib getNPCLib() {
        return NPCLib.getInstance();
    }

    /**
     * Automatically cache player skins for applying to NPCs later on.
     * Will be cached on login and removed on logout.
     * @param shouldCache - whether or not this plugin should automatically cache player skins
     */
    public static void autoCachePlayerSkins(boolean shouldCache) {
        NPCLib.setShouldCacheSkins(shouldCache);
    }

    /**
     * Check if an EntityPlayer is a registered NPC
     * @param player - EntityPlayer to check
     * @return isNPC
     */
    public static boolean isNPC(EntityPlayer player) {
        return NPCLib.getNPCEntities().containsKey(player);
    }

    /**
     * Gets an NPC using its ID given by this plugin
     * @param id - NPC ID
     * @return NPC
     */
    public static NPC getNPCById(Integer id) {
        return NPCLib.getNPCs().get(id);
    }

    /**
     * Gets an NPC Object from an EntityPLayer
     * @param entityPlayer - EntityPlayer that represents the NPC
     * @return NPC
     */
    public static NPC getNPCFromEntity(EntityPlayer entityPlayer) {
        if ((!entityPlayer.getBukkitEntity().hasMetadata("NPC_ID")) || entityPlayer.getBukkitEntity().getMetadata("NPC_ID").size() == 0) return null;
        return getNPCById(entityPlayer.getBukkitEntity().getMetadata("NPC_ID").get(0).asInt());
    }

    /**
     * Creates an NPC at the given location (considering pitch and yaw as well).
     * @param location - location for the NPC
     * @param skin - skin for the NPC
     * @param persistent - whether this NPC will be saved after restarts or not
     */
    public static NPC createNPC(Location location, NPCSkin skin, String name, boolean nameShown, boolean persistent, Pair<EnumItemSlot, ItemStack>... equipment) {
        Integer id = NPCLib.getNextId();
        NPC npc = new NPC(location, skin, id, UUID.randomUUID().toString(), name, nameShown,  true, persistent, equipment);
        if (persistent) {
            NPCLoader.saveNPC(npc, NPCLib.getFileConfig());
        }
        NPCLib.getNPCs().put(npc.getId(), npc);
        NPCLib.getNPCEntities().put(npc.getEntityPlayer(), npc);
        NPCHandler.createNPCForPlayers(npc);
        NPCHandler.placeNPCInGrid(npc);
        for (Player player : Bukkit.getOnlinePlayers()) {
            NPCScoreboardHandler.hideNPCNamesForPlayer(player);
        }
        NPCLib.updateNPCs();
        return npc;
    }

    /**
     * Creates an NPC at the given location (considering pitch and yaw as well).
     * Uses a skin that is already been cached.
     * @param location - location for the NPC
     * @param cachedSkinUUID - UUID of cached skin for the NPC
     * @param persistent - whether this NPC will be saved after restarts or not
     */
    public static NPC createNPC(Location location, UUID cachedSkinUUID, String name, boolean nameShown, boolean persistent, Pair<EnumItemSlot, ItemStack>... equipment) {
        if (!NPCLib.getCachedSkins().containsKey(cachedSkinUUID)) {
            throw new IllegalArgumentException("Cached Skin UUID has not been cached! Either manually cache it through the API, or enable automatic player skin caching!");
        }
        return createNPC(location, NPCLib.getCachedSkins().get(cachedSkinUUID), name, nameShown, persistent, equipment);
    }

    /**
     * Caches a player skin (will not remove it by itself though!)
     * Warning: This should not be run on the main thread!
     * @param uuid - UUID of player to cache skin of
     */
    public static void cachePlayerSkin(UUID uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false");
            Scanner scanner = new Scanner(url.openStream(), "UTF-8");
            Scanner withDelimiter = scanner.useDelimiter("\\A");
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(scanner.next());
            scanner.close();
            withDelimiter.close();
            JSONObject skinProperties = (JSONObject) ((JSONArray) jsonObject.get("properties")).get(0);
            NPCLib.getCachedSkins().put(uuid, new NPCSkin((String) skinProperties.get("value"), (String) skinProperties.get("signature")));
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.WARNING, "[NPCLib] Error loading player skin!");
            exception.printStackTrace();
        }
    }

    /**
     * Caches a player skin (will nto remove it by itself though!)
     * @param uuid - UUID of the player to cache skin of
     * @param skin - skin to cache
     */
    public static void cachePlayerSkin(UUID uuid, NPCSkin skin) {
        NPCLib.getCachedSkins().put(uuid, skin);
    }

    /**
     * Removes a cached player skin from the cache
     * @param uuid - UUID of player skin to remove
     */
    public static void removeSkinFromCache(UUID uuid) {
        NPCLib.getCachedSkins().remove(uuid);
    }

    /**
     * Fetches an NPCSkin from https://mineskin.org.
     * Warning: This should not be run on the main thread!
     * @param id - Mineskin ID of the skin you are retrieving
     * @return NPCSkin
     */
    public static NPCSkin getMineskinSkin(String id) {
        String url = "https://api.mineskin.org/get/id/" + id;
        try {
            Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8");
            Scanner withDelimiter = scanner.useDelimiter("\\A");
            JSONObject object = (JSONObject) JSONValue.parseWithException(scanner.next());
            JSONObject data = (JSONObject) object.get("data");
            JSONObject texture = (JSONObject) data.get("texture");
            String value = (String) texture.get("value");
            String signature = (String) texture.get("signature");
            scanner.close();
            withDelimiter.close();
            return new NPCSkin(value, signature);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes an NPC
     * @param id - ID of the NPC to delete (ID is assigned by this plugin, not EntityPlayer ID)
     */
    public static void deleteNPC(Integer id) {
        if (NPCLib.getNPCs().containsKey(id)) {
            NPC npc = NPCLib.getNPCs().get(id);
            npc.setShown(false);
            NPCLib.updateNPCs();
            npc.delete();
            NPCLib.getNPCs().remove(id);
            NPCLib.getNPCEntities().remove(npc.getEntityPlayer());
            NPCHandler.removeNPCForPlayers(npc);
            NPCHandler.removeNPCFromGrid(npc);
            for (Player player : Bukkit.getOnlinePlayers()) {
                NPCScoreboardHandler.hideNPCNamesForPlayer(player);
            }
            if (npc.isPersistent()) {
                NPCLoader.deleteNPC(id, NPCLib.getFileConfig());
            }
        } else {
            throw new IllegalArgumentException("That NPC ID does not exist!");
        }
    }

    /**
     * Deletes an NPC
     * @param entityPlayer - EntityPlayer that represents the NPC to delete
     */
    public static void deleteNPC(EntityPlayer entityPlayer) {
        if ((!entityPlayer.getBukkitEntity().hasMetadata("NPC_ID")) || entityPlayer.getBukkitEntity().getMetadata("NPC_ID").size() == 0) {
            throw new IllegalArgumentException("EntityPlayer provided is not a registered NPC!");
        }
        deleteNPC(entityPlayer.getBukkitEntity().getMetadata("NPC_ID").get(0).asInt());
    }

    /**
     * Deletes and NPC
     * @param npc - NPC object
     */
    public static void deleteNPC(NPC npc) {
        deleteNPC(npc.getId());
    }

}
