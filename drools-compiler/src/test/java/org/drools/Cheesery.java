package org.drools;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cheesery
    implements
    Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 320L;
    public final static int   MAKING_CHEESE    = 0;
    public final static int   SELLING_CHEESE   = 1;

    private final List        cheeses          = new ArrayList();

    private int               status;
    private int               totalAmount;
    private Maturity          maturity;

    public List getCheeses() {
        return this.cheeses;
    }

    public void addCheese(final Cheese cheese) {
        this.cheeses.add( cheese );
        this.totalAmount += cheese.getPrice();
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public Maturity getMaturity() {
        return this.maturity;
    }

    public void setMaturity(final Maturity maturity) {
        this.maturity = maturity;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(final int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static class Maturity {
        public static final Maturity YOUNG = new Maturity( "young" );
        public static final Maturity OLD   = new Maturity( "old" );

        private String               age;

        public Maturity(final String age) {
            this.age = age;
        }

        public String toString() {
            return "[Maturity age='" + this.age + "']";
        }
    }
}