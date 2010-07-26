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

package org.drools.examples.broker.model;


/**
 * A stock tick event informing of a state change due to some operation;
 * 
 * @author etirelli
 */
public class StockTick {
    private final String symbol;
    private final double price;
    private final long timestamp;
    private double delta;
    private String str;
    
    public StockTick(String symbol,
                     double price,
                     long timestamp) {
        super();
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.str = createString();
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public String toString() {
        return str;
    }

    private String createString() {
        return symbol+" $"+price+((delta<0)?" ":" +")+(((double)Math.round( delta*10000 ))/100)+"%";
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
        this.str = createString();
    }
}
