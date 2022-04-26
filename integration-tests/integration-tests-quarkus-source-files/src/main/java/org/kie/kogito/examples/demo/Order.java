/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples.demo;

public class Order implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private String orderNumber;
    private Boolean shipped;
    private Double total;

    public Order() {
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Boolean isShipped() {
        return this.shipped;
    }

    public void setShipped(Boolean shipped) {
        this.shipped = shipped;
    }

    public Double getTotal() {
        return this.total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Order(String orderNumber, Boolean shipped,
            Double total) {
        this.orderNumber = orderNumber;
        this.shipped = shipped;
        this.total = total;
    }

    public String toString() {
        return "Order[" + this.orderNumber + "]";
    }

}