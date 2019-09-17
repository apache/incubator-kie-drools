/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.flightcrewscheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Flight extends AbstractPersistable implements Comparable<Flight> {

    private static final Comparator<Flight> COMPARATOR = Comparator.comparing(Flight::getDepartureUTCDate)
            .thenComparing(Flight::getDepartureAirport)
            .thenComparing(Flight::getDepartureUTCDateTime)
            .thenComparing(Flight::getArrivalAirport)
            .thenComparing(Flight::getArrivalUTCDateTime);

    private String flightNumber;
    private Airport departureAirport;
    private LocalDateTime departureUTCDateTime;
    private Airport arrivalAirport;
    private LocalDateTime arrivalUTCDateTime;

    public Flight() {
    }

    public long getDurationInMinutes() {
        return ChronoUnit.MINUTES.between(departureUTCDateTime, arrivalUTCDateTime);
    }

    public LocalDate getDepartureUTCDate() {
        return departureUTCDateTime.toLocalDate();
    }
    public LocalTime getDepartureUTCTime() {
        return departureUTCDateTime.toLocalTime();
    }

    public LocalDate getArrivalUTCDate() {
        return arrivalUTCDateTime.toLocalDate();
    }
    public LocalTime getArrivalUTCTime() {
        return arrivalUTCDateTime.toLocalTime();
    }

    // TODO return overlapping time to avoid score trap?
    public boolean overlaps(Flight other) {
        return departureUTCDateTime.compareTo(other.arrivalUTCDateTime) < 0
            && other.departureUTCDateTime.compareTo(arrivalUTCDateTime) < 0;
    }

    @Override
    public String toString() {
        return flightNumber + "@" + departureUTCDateTime.toLocalDate();
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public LocalDateTime getDepartureUTCDateTime() {
        return departureUTCDateTime;
    }

    public void setDepartureUTCDateTime(LocalDateTime departureUTCDateTime) {
        this.departureUTCDateTime = departureUTCDateTime;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public LocalDateTime getArrivalUTCDateTime() {
        return arrivalUTCDateTime;
    }

    public void setArrivalUTCDateTime(LocalDateTime arrivalUTCDateTime) {
        this.arrivalUTCDateTime = arrivalUTCDateTime;
    }

    @Override
    public int compareTo(Flight o) {
        return COMPARATOR.compare(this, o);
    }
}
