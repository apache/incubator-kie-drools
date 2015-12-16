/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cheesery
    implements
    Serializable {
    private static final long serialVersionUID = 510l;
    public final static int   MAKING_CHEESE    = 0;
    public final static int   SELLING_CHEESE   = 1;

    private List<Cheese>      cheeses          = new ArrayList<Cheese>();

    private int               status;
    private int               totalAmount;
    private Maturity          maturity;

    public List<Cheese> getCheeses() {
        return this.cheeses;
    }
    public void setCheeses(List<Cheese> l) {
        this.cheeses=l;
    }

    public void addCheese(final Cheese cheese) {
        this.cheeses.add( cheese );
        this.totalAmount += cheese.getPrice();
    }

    public void removeCheese(final Cheese cheese) {
        this.cheeses.remove( cheese );
        recalculateTotalAmount();
    }

    private void recalculateTotalAmount() {
        this.totalAmount = 0;
        for( Cheese cheese : this.cheeses ) {
            this.totalAmount += cheese.getPrice();
        }
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(final int totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setTotalAmountToZero() {
        this.totalAmount = 0;
    }
    
    public Maturity getMaturity() {
        return this.maturity;
    }
    
    public void setMaturity( Maturity m ) {
        this.maturity = m;
    }
    
    public static enum Maturity {
        YOUNG, OLD;
    }
    
    public void setMaturityAndStatus( Maturity m, int status ) {
        this.maturity = m;
        this.status = status;
    }
}
