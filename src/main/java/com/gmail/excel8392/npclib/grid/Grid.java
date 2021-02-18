package com.gmail.excel8392.npclib.grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grid<E> {

    private final GridBounds bounds;
    private final Map<Short, Map<Short, Set<E>>> map;

    public Grid(GridBounds bounds) {
        this.bounds = bounds;
        this.map = new HashMap<>();
    }

    public void insertElement(GridLocation gridLocation, E element) {
        if (!this.map.containsKey(gridLocation.getX())) {
            this.map.put(gridLocation.getX(), new HashMap<>());
        }
        if (!this.map.get(gridLocation.getX()).containsKey(gridLocation.getY())) {
            this.map.get(gridLocation.getX()).put(gridLocation.getY(), new HashSet<E>());
        }
        this.map.get(gridLocation.getX()).get(gridLocation.getY()).add(element);
    }

    public Set<E> getSurroundingElements(GridLocation gridLocation, final short distance) {
        Set<E> elements = new HashSet<E>();
        for (short i = (short) (Math.abs(distance) * -1 + gridLocation.getX()); i < Math.abs(distance) + gridLocation.getX(); i++) {
            for (short j = (short) (Math.abs(distance) * -1 + gridLocation.getY()); j < Math.abs(distance) + gridLocation.getY(); j++) {
                if (!this.map.containsKey(i)) continue;
                if (!this.map.get(i).containsKey(j)) continue;
                elements.addAll(this.map.get(i).get(j));
            }
        }
        return elements;
    }

    public Set<E> getSurroundingElements(GridLocation gridLocation) {
        return this.getSurroundingElements(gridLocation, (short) 1);
    }

    public boolean containsElementInGrid(GridLocation location, E element) {
        if (!this.map.containsKey(location.getX())) return false;
        if (!this.map.get(location.getX()).containsKey(location.getY())) return false;
        return this.map.get(location.getX()).get(location.getY()).contains(element);
    }

    public Set<E> getElements(GridLocation gridLocation) {
        if (!this.map.containsKey(gridLocation.getX())) return new HashSet<E>();
        if (!this.map.get(gridLocation.getX()).containsKey(gridLocation.getY())) return new HashSet<E>();
        return map.get(gridLocation.getX()).get(gridLocation.getY());
    }

    public void removeElement(GridLocation gridLocation, E element) {
        if (!this.map.containsKey(gridLocation.getX())) return;
        if (!this.map.get(gridLocation.getX()).containsKey(gridLocation.getY())) return;
        this.map.get(gridLocation.getX()).get(gridLocation.getY()).remove(element);
    }

    public GridBounds getBounds() {
        return this.bounds;
    }

}
