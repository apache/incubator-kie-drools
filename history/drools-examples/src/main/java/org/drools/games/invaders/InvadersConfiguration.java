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

package org.drools.games.invaders;

import org.drools.games.GameConfiguration;

public class InvadersConfiguration extends GameConfiguration {

    private int shipWidth;
    private int shipHeight;
    private int shipSpeed;


    private int bulletWidth;
    private int bulletHeight;
    private int bulletSpeed;

    private int invader1Width;
    private int invader1Height;
    private int invader1Speed;

    public InvadersConfiguration() {
        super();

        setShipWidth(61);
        setShipHeight(39);
        setShipSpeed(4);

        setBulletWidth(4);
        setBulletHeight(20);
        setBulletSpeed(8);

        setInvader1Width(41);
        setInvader1Height(44);
        setInvader1Speed(2);
    }

    public int getShipWidth() {
        return shipWidth;
    }

    public void setShipWidth(int shipWidth) {
        this.shipWidth = shipWidth;
    }

    public int getShipHeight() {
        return shipHeight;
    }

    public void setShipHeight(int shipHeight) {
        this.shipHeight = shipHeight;
    }

    public int getShipSpeed() {
        return shipSpeed;
    }

    public void setShipSpeed(int shipSpeed) {
        this.shipSpeed = shipSpeed;
    }

    public int getBulletWidth() {
        return bulletWidth;
    }

    public void setBulletWidth(int bulletWidth) {
        this.bulletWidth = bulletWidth;
    }

    public int getBulletHeight() {
        return bulletHeight;
    }

    public void setBulletHeight(int bulletHeight) {
        this.bulletHeight = bulletHeight;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public int getInvader1Width() {
        return invader1Width;
    }

    public void setInvader1Width(int invader1Width) {
        this.invader1Width = invader1Width;
    }

    public int getInvader1Height() {
        return invader1Height;
    }

    public void setInvader1Height(int invader1Height) {
        this.invader1Height = invader1Height;
    }

    public int getInvader1Speed() {
        return invader1Speed;
    }

    public void setInvader1Speed(int invader1Speed) {
        this.invader1Speed = invader1Speed;
    }
}
