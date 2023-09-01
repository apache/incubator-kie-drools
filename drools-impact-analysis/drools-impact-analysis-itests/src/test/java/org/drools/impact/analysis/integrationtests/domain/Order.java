/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.impact.analysis.integrationtests.domain;

public class Order {

    private long id;

    private String itemName;

    private double itemPrice;

    private int customerAge;

    private int customerMembershipRank;

    private double discount = 0;

    private String status;

    public Order(long id, String itemName, double itemPrice, int customerAge, int customerMembershipRank) {
        super();
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.customerAge = customerAge;
        this.customerMembershipRank = customerMembershipRank;
        this.status = "Ordered";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(int customerAge) {
        this.customerAge = customerAge;
    }

    public int getCustomerMembershipRank() {
        return customerMembershipRank;
    }

    public void setCustomerMembershipRank(int customerMembershipRank) {
        this.customerMembershipRank = customerMembershipRank;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", itemName=" + itemName + ", itemPrice=" + itemPrice + ", customerAge=" + customerAge + ", customerMembershipRank=" + customerMembershipRank + ", discount=" + discount + ", status=" +
               status + "]";
    }

}
