package org.drools.games.invaders;

public class Invader extends Unit {
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
