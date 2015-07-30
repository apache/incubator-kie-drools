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

package org.drools.games.numberguess;

public class GameRules {
    private int maxRange;
    private int allowedGuesses;

    public GameRules(int maxRange,
                     int allowedGuesses) {
        this.maxRange = maxRange;
        this.allowedGuesses = allowedGuesses;
    }

    public int getAllowedGuesses() {
        return allowedGuesses;
    }

    public int getMaxRange() {
        return maxRange;
    }

    @Override
    public String toString() {
        return "GameRules{" +
               "maxRange=" + maxRange +
               ", allowedGuesses=" + allowedGuesses +
               '}';
    }
}
