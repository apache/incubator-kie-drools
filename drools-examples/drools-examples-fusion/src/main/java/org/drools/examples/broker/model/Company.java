/*
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
 * A POJO for a company
 * 
 * @author etirelli
 */
public class Company {

    private String name;
    private String symbol;
    private double currentPrice;
    private double previousPrice;

    public Company(String name,
                   String symbol) {
        this( name,
              symbol,
              0,
              0 );
    }

    public Company(String name,
                   String symbol,
                   double current,
                   double previous) {
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = current;
        this.previousPrice = previous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double current) {
        this.previousPrice = this.currentPrice;
        this.currentPrice = current;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }
    
    public double getDelta() {
        return ( previousPrice == 0 ) ? 0.0 : (( currentPrice / previousPrice ) - 1.0);
    }

}
