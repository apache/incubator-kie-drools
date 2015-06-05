package org.optaplanner.examples.nqueens.integration.util;

public class QueenCoordinates  {

    public static QueenCoordinates createQueenCoordinates(int x, int y) {
        return new QueenCoordinates(x, y);
    }

    private final int x;
    private final int y;

    public QueenCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
