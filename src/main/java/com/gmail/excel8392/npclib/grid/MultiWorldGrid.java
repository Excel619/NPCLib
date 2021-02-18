package com.gmail.excel8392.npclib.grid;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiWorldGrid<E> {

    private Map<World, WorldGrid<E>> grids;

    private final GridBounds bounds;
    private final short blocksPerBox;

    public MultiWorldGrid(GridBounds bounds, short blocksPerBox) {
        this.grids = new HashMap<>();
        this.bounds = bounds;
        this.blocksPerBox = blocksPerBox;
    }

    public Set<E> getSurroundingElements(Location location, short distance) {
        if (!this.grids.containsKey(location.getWorld())) return new HashSet<E>();
        return this.grids.get(location.getWorld()).getSurroundingElements(location, distance);
    }

    public Set<E> getSurroundingElements(Location location) {
        if (!this.grids.containsKey(location.getWorld())) return new HashSet<E>();
        return this.grids.get(location.getWorld()).getSurroundingElements(location);
    }

    public boolean containsElementInGrid(Location location, E element) {
        if (!this.grids.containsKey(location.getWorld())) return false;
        return this.grids.get(location.getWorld()).containsElementInGrid(location, element);
    }

    public void insertElement(Location location, E element) {
        if (!this.grids.containsKey(location.getWorld())) this.grids.put(location.getWorld(), new WorldGrid<E>(this.bounds, this.blocksPerBox));
        this.grids.get(location.getWorld()).insertElement(location, element);
    }

    public void removeElement(Location location, E element) {
        if (!this.grids.containsKey(location.getWorld())) return;
        this.grids.get(location.getWorld()).removeElement(location, element);
    }

    public Set<E> getElements(Location location) {
        if (!this.grids.containsKey(location.getWorld())) return new HashSet<E>();
        return this.grids.get(location.getWorld()).getElements(location);
    }

    public Set<E> getSurroundingElements(World world, GridLocation gridLocation, final short distance) {
        if (!this.grids.containsKey(world)) return new HashSet<E>();
        return this.grids.get(world).getSurroundingElements(gridLocation, distance);
    }

    public Set<E> getSurroundingElements(World world, GridLocation gridLocation) {
        return this.getSurroundingElements(world, gridLocation, (short) 1);
    }

    public boolean containsElementInGrid(World world, GridLocation location, E element) {
        if (!this.grids.containsKey(world)) return false;
        return this.grids.get(world).containsElementInGrid(location, element);
    }

    public void insertElement(World world, GridLocation gridLocation, E element) {
        if (!this.grids.containsKey(world)) this.grids.put(world, new WorldGrid<E>(this.bounds, this.blocksPerBox));
        this.grids.get(world).insertElement(gridLocation, element);
    }

    public void removeElement(World world, GridLocation gridLocation, E element) {
        if (!this.grids.containsKey(world)) return;
        this.grids.get(world).removeElement(gridLocation, element);
    }

    public Set<E> getElements(World world, GridLocation gridLocation) {
        if (!this.grids.containsKey(world)) return new HashSet<E>();
        return this.grids.get(world).getElements(gridLocation);
    }

    public GridBounds getBounds() {
        return this.bounds;
    }

    public short getBlocksPerBox() {
        return this.blocksPerBox;
    }

    public WorldGrid<E> getGrid(World world) {
        return this.grids.get(world);
    }

    public Map<World, WorldGrid<E>> getGrids() {
        return this.grids;
    }

}
