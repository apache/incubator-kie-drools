package org.drools.games.pong;

import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Ball {
    private int x;
    private int y;
    private int width;
    
    private int dx;
    private int dy;
    
    private int speed;
    
    public Ball(int x,
                int y,
                int width) {
        this.x = x;        
        this.y = y;
        this.width = width;      
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }   

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Ball [x=" + x + ", y=" + y + ", width=" + width + ", dx=" + dx + ", dy=" + dy + ", speed=" + speed + "]";
    }  
    
}
