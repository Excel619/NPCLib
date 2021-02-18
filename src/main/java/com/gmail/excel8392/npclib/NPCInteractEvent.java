package com.gmail.excel8392.npclib;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private final NPC npc;
    private final Player player;
    private final NPCInteractType interactType;

    private static final HandlerList handlers = new HandlerList();

    public NPCInteractEvent(NPC npc, Player player, NPCInteractType interactType) {
        this.npc = npc;
        this.player = player;
        this.interactType = interactType;
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

    public NPCInteractType getInteractType() {
        return this.interactType;
    }

}
