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
package org.acme.insurance;

/**
 * This represents a policy that a driver is applying for. 
 * 
 * Obviously in the real world, there are actuaries to mess things up, but lets just pretend there is
 * some simple base price and discount that we can calculate with relatively simple rules !
 */
public class Policy {

    private String  type            = "COMPREHENSIVE";
    private boolean approved        = false;
    private int     discountPercent = 0;
    private int     basePrice;

    public boolean isApproved() {
        return this.approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public int getDiscountPercent() {
        return this.discountPercent;
    }

    public void setDiscountPercent(final int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void applyDiscount(final int discount) {
        this.discountPercent += discount;
    }

    public int getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(final int basePrice) {
        this.basePrice = basePrice;
    }

}
