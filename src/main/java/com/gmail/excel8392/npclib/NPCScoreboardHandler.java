package com.gmail.excel8392.npclib;

import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.ScoreboardTeam;
import net.minecraft.server.v1_16_R3.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NPCScoreboardHandler implements Listener {

    private static final List<String> npcNames = new ArrayList<>();
    private static ScoreboardTeam team;

    public static void initScoreboard() {
        for (Map.Entry<Integer, NPC> entry : NPCLib.getNPCs().entrySet()) {
            npcNames.add(entry.getValue().getEntityPlayer().getName());
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        team = new ScoreboardTeam(((CraftScoreboard) scoreboard).getHandle(), "npcs");
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
    }

    public static void addNPCName(NPC npc) {
        npcNames.add(npc.getEntityPlayer().getName());
    }

    public static void removeNPCName(NPC npc) {
        npcNames.remove(npc.getEntityPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        hideNPCNamesForPlayer(event.getPlayer());
    }

    public static void hideNPCNamesForPlayer(Player player) {
        if (npcNames.size() > 0) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, npcNames, 3));
        }
    }

}
