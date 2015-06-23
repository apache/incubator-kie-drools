/*
 * Copyright 2015 JBoss Inc
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

import org.drools.games.GameConfiguration;

public class PongConfiguration extends GameConfiguration {
    private int padding;
    
    private int sideLineWidth;

    private int netWidth;
    private int netDash;
    private int netGap;
    
    private int ballWidth;
    private int ballStartingSpeed;
    
    private int batWidth;
    private int batHeight;
    private int batSpeed;

    public PongConfiguration() {
        setTableWidth( 700 );
        setTableHeight( 500 );
        setSideLineWidth( 10 );
        
        setBallWidth( 20 );
        setBallStartingSpeed( 1 );
        
        setBatWidth( 5 );
        setBatHeight( 80 );
        setBatSpeed( 2 );
        setPadding( 10 );
        setNet( 2, 20, 10 );
    }

    public int getTableWidth() {
        return getWindowWidth();
    }

    public void setTableWidth(int tableWidth) {
        validTableDimension( tableWidth );
        setWindowWidth(tableWidth);
    }

    public int getTableHeight() {
        return getWindowHeight();
    }

    public void setTableHeight(int tableHeight) {
        validTableDimension( tableHeight );
        setWindowHeight(tableHeight);
    }

    private void validTableDimension(int dimension) {
        if ( dimension % 20 != 0 ) {
            throw new IllegalArgumentException( "Table dimensions must be divisiable by 20" );
        }
    }

    public int getSideLineWidth() {
        return sideLineWidth;
    }

    public void setSideLineWidth(int sideLineWidth) {
        this.sideLineWidth = sideLineWidth;
    }    
    
    public int getBallWidth() {
        return ballWidth;
    }

    public void setBallWidth(int ballWidth) {
        this.ballWidth = ballWidth;
    }

    public int getBallStartingSpeed() {
        return ballStartingSpeed;
    }

    public void setBallStartingSpeed(int ballStartingSpeed) {
        this.ballStartingSpeed = ballStartingSpeed;
    }

    public int getBatWidth() {
        return batWidth;
    }

    public void setBatWidth(int batWidth) {
        this.batWidth = batWidth;
    }

    public int getBatHeight() {
        return batHeight;
    }

    public void setBatHeight(int batHeight) {
        this.batHeight = batHeight;
    }
    
    public int getBatSpeed() {
        return batSpeed;
    }

    public void setBatSpeed(int batSpeed) {
        this.batSpeed = batSpeed;
    }    

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setNet(int width,
                       int dash,
                       int gap) {
        if ( width % 2 != 0 ) {
            throw new IllegalArgumentException( "Fence Width must be divisiable by 2" );
        }
        this.netWidth = width;

        this.netDash = dash;
        this.netGap = gap;
    }

    public int getNetWidth() {
        return netWidth;
    }

    public int getNetDash() {
        return netDash;
    }

    public int getNetGap() {
        return netGap;
    }

    public int boundedRight() {
        return getTableWidth() - getPadding();
    }

    public int boundedLeft() {
        return getPadding();
    }

    public int boundedBottom() {
        return getTableHeight() - getPadding() - getSideLineWidth();
    }

    public int boundedTop() {
        return getPadding() + getSideLineWidth() ;
    }

}
