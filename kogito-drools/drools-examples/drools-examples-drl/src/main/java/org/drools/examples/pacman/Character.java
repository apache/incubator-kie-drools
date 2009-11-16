package org.drools.examples.pacman;

public class Character {
    String name;
    
    private int speed;
    
    public Character( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
   
    @Override
    public String toString() {
        return "pacman speed = " + getSpeed();
    }
}
