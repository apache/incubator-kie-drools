/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.qa.bpms.dm.iberia;

public class Reservation extends java.lang.Object implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private java.lang.String flight;

    private org.jboss.qa.bpms.dm.iberia.Passenger passenger;

    private java.lang.Integer seat;

    public Reservation() {
    }

    public Reservation(java.lang.String flight, org.jboss.qa.bpms.dm.iberia.Passenger passenger, java.lang.Integer seat) {
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
    }

    public java.lang.String getFlight() {
        return this.flight;
    }

    public void setFlight(java.lang.String flight) {
        this.flight = flight;
    }
    
    public org.jboss.qa.bpms.dm.iberia.Passenger getPassenger() {
        return this.passenger;
    }

    public void setPassenger(org.jboss.qa.bpms.dm.iberia.Passenger passenger) {
        this.passenger = passenger;
    }
    
    public java.lang.Integer getSeat() {
        return this.seat;
    }

    public void setSeat(java.lang.Integer seat) {
        this.seat = seat;
    }
}