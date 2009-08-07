/*
 * Copyright 2007 JBoss Inc
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
 * Created on Oct 17, 2007
 */
package org.drools;

import java.io.Serializable;

/**
 * A simple order event POJO
 * 
 * @author etirelli
 */
public class OrderEvent implements Serializable {

    private String id;
    private String customer;
    private double total;

    public OrderEvent(String id,
                      String customer,
                      double total) {
        super();
        this.id = id;
        this.customer = customer;
        this.total = total;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderEvent( id="+id+" customer="+customer+" total="+total+" )";
    }
}
