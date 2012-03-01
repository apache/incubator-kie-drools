package org.drools.games.wumpus;

import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Hero extends Thing {

    private Direction direction;
    private boolean   gold;
    private int       arrows;
    private int       score;

    public Hero(int row,
                int col) {
        super( row, col );
        this.arrows = 1;
        this.direction = Direction.RIGHT;
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

    public boolean isGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }

    @Override
    public String toString() {
        return "Hero [direction=" + direction + ", gold=" + gold + ", arrows=" + arrows + ", score=" + score + "]";
    }

}
