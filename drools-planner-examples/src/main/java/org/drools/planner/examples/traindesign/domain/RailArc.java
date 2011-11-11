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

@XStreamAlias("RailArc")
public class RailArc extends AbstractPersistable {

    private RailNode origin;
    private RailNode destination;
    private int distance; // in miles * 1000 (to avoid Double rounding errors and BigDecimal)
    private int maximumTrainLength; // in feet
    private int maximumTonnage; // in tons
    private int maximumNumberOfTrains;

    private RailArc reverse;

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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMaximumTrainLength() {
        return maximumTrainLength;
    }

    public void setMaximumTrainLength(int maximumTrainLength) {
        this.maximumTrainLength = maximumTrainLength;
    }

    public int getMaximumTonnage() {
        return maximumTonnage;
    }

    public void setMaximumTonnage(int maximumTonnage) {
        this.maximumTonnage = maximumTonnage;
    }

    public int getMaximumNumberOfTrains() {
        return maximumNumberOfTrains;
    }

    public void setMaximumNumberOfTrains(int maximumNumberOfTrains) {
        this.maximumNumberOfTrains = maximumNumberOfTrains;
    }

    public RailArc getReverse() {
        return reverse;
    }

    public void setReverse(RailArc reverse) {
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return origin + "->" + destination;
    }

}
