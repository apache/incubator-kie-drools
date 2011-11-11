/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.traindesign.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CarBlock")
public class CarBlock extends AbstractPersistable {

    private String code;
    private RailNode origin;
    private RailNode destination;
    private int numberOfCars;
    private int length; // in feet
    private int tonnage; // in tons

    // TODO Isn't always correct in the problem xls's.
    private int shortestDistance; // in miles * 1000 (to avoid Double rounding errors and BigDecimal)

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RailNode getOrigin() {
        return origin;
    }

    public void setOrigin(RailNode origin) {
        this.origin = origin;
    }

    public RailNode getDestination() {
        return destination;
    }

    public void setDestination(RailNode destination) {
        this.destination = destination;
    }

    public int getNumberOfCars() {
        return numberOfCars;
    }

    public void setNumberOfCars(int numberOfCars) {
        this.numberOfCars = numberOfCars;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getTonnage() {
        return tonnage;
    }

    public void setTonnage(int tonnage) {
        this.tonnage = tonnage;
    }

    public int getShortestDistance() {
        return shortestDistance;
    }

    public void setShortestDistance(int shortestDistance) {
        this.shortestDistance = shortestDistance;
    }

    @Override
    public String toString() {
        return code + "(" + origin + "->" + destination + ")";
    }

}
