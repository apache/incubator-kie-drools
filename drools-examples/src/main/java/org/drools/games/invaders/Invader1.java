package org.drools.games.invaders;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Invader1 extends Unit {
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
