package com.gmail.excel8392.npclib.grid;

public class GridBounds {

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public GridBounds(int x1, int y1, int x2, int y2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public boolean isInBounds(int x, int y) {
        return x >= this.getX1() &&
                x <= this.getX2() &&
                y >= this.getY1() &&
                y <= this.getY2();
    }

}
