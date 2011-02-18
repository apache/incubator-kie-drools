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
 * @author etirelli
 *
 */
public class PortfolioAction {
    private Action action;
    private String symbol;
    private int quant;
    
    public PortfolioAction() {
        super();
    }
    public PortfolioAction(Action action,
                           String symbol,
                           int quant) {
        super();
        this.action = action;
        this.symbol = symbol;
        this.quant = quant;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public int getQuant() {
        return quant;
    }
    public void setQuant(int quant) {
        this.quant = quant;
    }

    @Override
    public String toString() {
    	return "PortfolioAction( "+action+" "+symbol+" "+quant+ " )";
    }
    


}
