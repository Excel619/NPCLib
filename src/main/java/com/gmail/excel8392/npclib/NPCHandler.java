package com.gmail.excel8392.npclib;

import com.gmail.excel8392.gridlib.GridBounds;
import com.gmail.excel8392.gridlib.MultiWorldGrid;
import com.gmail.excel8392.gridlib.WorldGrid;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NPCHandler implements Listener {

    private static final Map<Player, Map<NPC, Boolean>> loadedNPCs = new HashMap<>();

    private static final MultiWorldGrid<NPC> grid = new MultiWorldGrid<>(new GridBounds(-4096, -4096, 4096, 4096), (short) 32);

    public static void placeNPCsInGrid(Map<Integer, NPC> npcs) {
        for (Map.Entry<Integer, NPC> entry : npcs.entrySet()) {
            grid.insertElement(entry.getValue().getLocation(), entry.getValue());
        }
    }

    public static void removeNPCFromGrid(NPC npc) {
        WorldGrid<NPC> worldGrid = grid.getGrid(npc.getLocation().getWorld());
        if (worldGrid == null) return;
        if (worldGrid.containsElementInGrid(npc.getLocation(), npc)) {
            grid.removeElement(npc.getLocation(), npc);
            return;
        }
        throw new IllegalArgumentException("NPC not in grid!");
    }

    public static void createNPCForPlayers(NPC npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadedNPCs.get(player).put(npc, false);
        }
    }

    public static void removeNPCForPlayers(NPC npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadedNPCs.get(player).remove(npc);
        }
    }

    public static void placeNPCInGrid(NPC npc) {
        grid.insertElement(npc.getLocation(), npc);
    }

    public static void updateNPCsForPlayer(Player player) {
        Set<NPC> surrounding = grid.getSurroundingElements(player.getLocation());
        for (Map.Entry<NPC, Boolean> entry : loadedNPCs.get(player).entrySet()) {
            if (entry.getValue()) {
                if ((!surrounding.contains(entry.getKey())) || (!entry.getKey().isShown())) {
                    entry.getKey().despawnForPlayer(player);
                    loadedNPCs.get(player).put(entry.getKey(), false);
                }
            } else {
                if (surrounding.contains(entry.getKey()) && entry.getKey().isShown()) {
                    entry.getKey().spawnForPlayer(player);
                    loadedNPCs.get(player).put(entry.getKey(), true);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(NPCLib.getInstance(), () -> {
            HashMap<NPC, Boolean> npcs = new HashMap<>();
            for (Map.Entry<EntityPlayer, NPC> entry : NPCLib.getNPCEntities().entrySet()) {
                npcs.put(entry.getValue(), false);
            }
            loadedNPCs.put(event.getPlayer(), npcs);
            updateNPCsForPlayer(event.getPlayer());
        }, 1);
        if (NPCLib.shouldCacheSkins()) {
            Bukkit.getScheduler().runTaskAsynchronously(NPCLib.getInstance(), () -> NPCLibAPI.cachePlayerSkin(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        loadedNPCs.remove(event.getPlayer());
        if (!NPCLib.shouldCacheSkins()) {
            if (NPCLib.getCachedSkins().containsKey(event.getPlayer().getUniqueId())) {
                NPCLib.getCachedSkins().remove(event.getPlayer().getUniqueId());
            }
        }
    }

    public static boolean hasLoadedDataForPlayer(Player player) {
        return loadedNPCs.containsKey(player);
    }

    public static MultiWorldGrid<NPC> getGrid() {
        return grid;
    }

}