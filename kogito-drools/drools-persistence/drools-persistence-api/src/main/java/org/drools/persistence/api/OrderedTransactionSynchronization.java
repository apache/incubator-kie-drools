/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.api;

public abstract class OrderedTransactionSynchronization implements TransactionSynchronization, Comparable<OrderedTransactionSynchronization> {

    private Integer order;
    private String identifier;

    public OrderedTransactionSynchronization(Integer order, String identifier) {
        this.order = order;
        this.identifier = identifier;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int compareTo(OrderedTransactionSynchronization o) {
        if (this.getClass() != o.getClass()) {
            return this.getOrder().compareTo(o.getOrder()+1);
        }
        int result = this.getOrder().compareTo(o.getOrder());
        if (result == 0) {
            return this.getIdentifier().compareTo(o.getIdentifier());
        }

        return result;
    }
}
