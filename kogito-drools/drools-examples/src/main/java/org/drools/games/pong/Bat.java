package org.drools.games.pong;


import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Bat {
    
    private Player        player;
    private int           x;    
    private int           y;
    private int           width;
    private int           height;
    private int           speed;

    public Bat(int x,
               int y,
               int width,
               int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }        

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public PlayerId getPlayerId() {
        return player.getId();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Bat [player=" + player + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", speed=" + speed + "]";
    }
     
}
