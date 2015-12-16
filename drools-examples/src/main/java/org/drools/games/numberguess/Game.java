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

package org.drools.games.numberguess;

public class Game {
    private int biggest;
    private int smallest;
    private int guessCount;

    public void begin() {
        this.guessCount = 0;
        this.biggest = 0;
        this.smallest = 100;
    }

    public void incrementGuessCount() {
        guessCount++;
    }

    public int getBiggest() {
        return this.biggest;
    }

    public int getSmallest() {
        return this.smallest;
    }

    public int getGuessCount() {
        return this.guessCount;
    }

    public void setGuessCount(int guessCount) {
        this.guessCount = guessCount;
    }

    public void setBiggest(int biggest) {
        this.biggest = biggest;
    }

    public void setSmallest(int smallest) {
        this.smallest = smallest;
    }

    @Override
    public String toString() {
        return "Game{" +
               "biggest=" + biggest +
               ", smallest=" + smallest +
               ", guessCount=" + guessCount +
               '}';
    }
}
