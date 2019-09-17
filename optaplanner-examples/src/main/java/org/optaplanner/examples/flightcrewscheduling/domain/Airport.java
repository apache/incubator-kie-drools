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

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Airport extends AbstractPersistable implements Comparable<Airport> {

    private String code; // IATA 3-letter code
    private String name;

    private double latitude;
    private double longitude;

    private Map<Airport, Long> taxiTimeInMinutesMap;

    public Airport() {
    }

    /**
     * @param other never null
     * @return null if no taxi connection
     */
    public Long getTaxiTimeInMinutesTo(Airport other) {
        return taxiTimeInMinutesMap.get(other);
    }

    public double getHaversineDistanceInKmTo(Airport other) {
        if (this == other) {
            return 0.0;
        }
        final int EARTH_RADIUS_IN_KM = 6371;
        final int TWICE_EARTH_RADIUS_IN_KM = 2 * EARTH_RADIUS_IN_KM;

        double latitudeInRads  = Math.toRadians(latitude);
        double longitudeInRads = Math.toRadians(longitude);
        // Cartesian coordinates, normalized for a sphere of diameter 1.0
        double cartesianX = 0.5 * Math.cos(latitudeInRads) * Math.sin(longitudeInRads);
        double cartesianY = 0.5 * Math.cos(latitudeInRads) * Math.cos(longitudeInRads);
        double cartesianZ = 0.5 * Math.sin(latitudeInRads);

        double otherLatitudeInRads  = Math.toRadians(other.latitude);
        double otherLongitudeInRads = Math.toRadians(other.longitude);
        // Cartesian coordinates, normalized for a sphere of diameter 1.0
        double otherCartesianX = 0.5 * Math.cos(otherLatitudeInRads) * Math.sin(otherLongitudeInRads);
        double otherCartesianY = 0.5 * Math.cos(otherLatitudeInRads) * Math.cos(otherLongitudeInRads);
        double otherCartesianZ = 0.5 * Math.sin(otherLatitudeInRads);

        // TODO cache the part above
        double dX = cartesianX - otherCartesianX;
        double dY = cartesianY - otherCartesianY;
        double dZ = cartesianZ - otherCartesianZ;
        double r = Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
        return TWICE_EARTH_RADIUS_IN_KM * Math.asin(r);
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<Airport, Long> getTaxiTimeInMinutesMap() {
        return taxiTimeInMinutesMap;
    }

    public void setTaxiTimeInMinutesMap(Map<Airport, Long> taxiTimeInMinutesMap) {
        this.taxiTimeInMinutesMap = taxiTimeInMinutesMap;
    }

    @Override
    public int compareTo(Airport o) {
        return code.compareTo(o.code);
    }
}
