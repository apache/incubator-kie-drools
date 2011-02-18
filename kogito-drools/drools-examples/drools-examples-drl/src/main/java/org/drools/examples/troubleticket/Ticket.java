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

/**
 * 
 */
package org.drools.examples.troubleticket;

public class Ticket {
    private Customer customer;
    private String   status;

    public Ticket() {

    }

    public Ticket(final Customer customer) {
        super();
        this.customer = customer;
        this.status = "New";
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public String toString() {
        return "[Ticket " + this.customer + " : " + this.status + "]";
    }

}
