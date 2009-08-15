/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.examples.broker.model;

/**
 * @author etirelli
 *
 */
public class SuddenDropEvent {
    
    private String symbol;
    private double percent;
    private long timestamp;
    
    public SuddenDropEvent() {
        super();
    }
    
    public SuddenDropEvent(String symbol,
                           double percent,
                           long timestamp) {
        super();
        this.symbol = symbol;
        this.percent = percent;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public double getPercent() {
        return percent;
    }
    public void setPercent(double percent) {
        this.percent = percent;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    

}
