package com.gmail.excel8392.npclib;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private final NPC npc;
    private final Player player;

    private static final HandlerList handlers = new HandlerList();

    public NPCInteractEvent(NPC npc, Player player) {
        this.npc = npc;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public NPC getNPC() {
        return this.npc;
    }

    public Player getPlayer() {
        return this.player;
    }

}
