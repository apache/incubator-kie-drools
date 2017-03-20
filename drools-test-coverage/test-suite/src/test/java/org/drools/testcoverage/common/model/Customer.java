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

public class Customer implements Serializable {

    private static final long serialVersionUID = -1247190303439997770L;
    private int custid;
    private String name;
    private int sales;
    private boolean tenthSaleFree;

    public Customer(int custid, String name) {
        super();
        this.custid = custid;
        this.name = name;
        sales = 0;
        tenthSaleFree = true;
    }

    public int getCustid() {
        return custid;
    }

    public String getName() {
        return name;
    }

    public int getSales() {
        return sales;
    }

    public void incrementSales() {
        sales++;
    }

    public boolean isTenthSaleFree() {
        return tenthSaleFree;
    }

    public void setTenthSaleFree(boolean tenthSaleFree) {
        this.tenthSaleFree = tenthSaleFree;
    }

    @Override
    public String toString() {
        return custid + " - " + name + " - " + sales;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer oCust = (Customer) o;
        return custid == oCust.custid;
    }

    @Override
    public int hashCode() {
        return custid;
    }
}
