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

import java.util.Random;

public class RandomNumber {
    private int randomNumber;

    public RandomNumber(int v) {
        this.randomNumber = new Random().nextInt( v );
    }

    public int getValue() {
        return this.randomNumber;
    }

    @Override
    public String toString() {
        return "RandomNumber{" +
               "randomNumber=" + randomNumber +
               '}';
    }
}
