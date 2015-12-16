/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.pong;


import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Bat {
    
    private Player        player;
    private int           x;    
    private int           y;
    private int           dy;
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

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
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
