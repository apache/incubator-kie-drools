package org.drools.examples.pacman;

public class Monster extends Character {
    @Override
    public String toString() {
        return "monster speed = " + getSpeed();
    }
}
