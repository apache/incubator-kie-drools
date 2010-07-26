/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.broker.misc;

import java.text.DecimalFormat;
import java.util.Random;

import org.drools.examples.broker.model.Action;

public class Utils {
    private static final Random rand = new Random(System.currentTimeMillis()); 
    
    public static String percent( double number ) {
        return new DecimalFormat( "0.00%" ).format( number );
    }
    
    public static Action selectAction() {
        int action = rand.nextInt( 3 );
        switch (action) {
            case 1 : return Action.BUY;
            case 2 : return Action.SELL;
            default : return Action.NOACTION;
        }
    }
}
