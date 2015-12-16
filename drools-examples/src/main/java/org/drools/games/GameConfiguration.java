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

package org.drools.games;

public class GameConfiguration {

    private boolean exitOnClose;

    private int windowWidth;
    private int windowHeight;

    public GameConfiguration(int width, int height) {
        setWindowWidth( 700 );
        setWindowHeight( 500 );
    }

    public GameConfiguration() {
        this(700, 500 );
    }

    public boolean isExitOnClose() {
        return exitOnClose;
    }

    public void setExitOnClose(boolean exitOnClose) {
        this.exitOnClose = exitOnClose;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int tableWidth) {
        validTableDimension( tableWidth );
        this.windowWidth = tableWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int tableHeight) {
        validTableDimension( tableHeight );
        this.windowHeight = tableHeight;
    }

    private void validTableDimension(int dimension) {
        if ( dimension % 20 != 0 ) {
            throw new IllegalArgumentException( "Table dimensions must be divisiable by 20" );
        }
    }

}
