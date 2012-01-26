package org.drools.examples.wumpus;

public class Hero {
    private int row;
    private int col;
    private Direction direction;
    private int arrows;
    private int score;

    public Hero(int row,
                int col) {
        this.row = row;
        this.col = col;
        this.arrows = 1;
        this.direction = Direction.RIGHT;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getArrows() {
        return arrows;
    }

    public void setArrows(int arrows) {
        this.arrows = arrows;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Hero [row=" + row + ", col=" + col + ", direction=" + direction + ", arrows=" + arrows + ", score=" + score + "]";
    }

}
