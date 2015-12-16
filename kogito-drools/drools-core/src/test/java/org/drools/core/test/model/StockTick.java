/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.test.model;

import java.io.Serializable;

public class StockTick implements Serializable {
    private long seq;
    private String company;
    private double price;
    private long time;
    private long duration;
    
    public StockTick() {
    }

    public StockTick(long seq,
                     String company,
                     double price,
                     long time) {
        super();
        this.seq = seq;
        this.company = company;
        this.price = price;
        this.time = time;
    }

    public StockTick(long seq,
                     String company,
                     double price,
                     long time, 
                     long duration ) {
        super();
        this.seq = seq;
        this.company = company;
        this.price = price;
        this.time = time;
        this.duration = duration;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public long getSeq() {
        return seq;
    }
    public void setSeq(long seq) {
        this.seq = seq;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public String toString() {
        return "StockTick( "+this.seq+" : " +this.company +" : "+ this.price +" )";
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

}
