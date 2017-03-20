/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Sale implements Serializable {

    private static final long serialVersionUID = 856715964777288208L;
    private final int saleid;
    private final int custid;
    private final int amount;
    private int discount;
    private boolean rebated = false;

    public Sale(int saleid, int custid, int amount) {
        super();
        this.saleid = saleid;
        this.custid = custid;
        this.amount = amount;
    }

    public int getSaleid() {
        return saleid;
    }

    public int getCustid() {
        return custid;
    }

    public int getAmount() {
        return amount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isRebated() {
        return rebated;
    }

    public void setRebated() {
        rebated = true;
    }

    @Override
    public String toString() {
        return saleid + " - " + custid + " - " + amount + " - " + discount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sale)) {
            return false;
        }
        Sale oSale = (Sale) o;
        return saleid == oSale.saleid;
    }

    @Override
    public int hashCode() {
        return saleid;
    }
}
