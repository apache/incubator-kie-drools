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

package org.drools.planner.examples.trailerrouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TrailerRoutingTrailer")
public class TrailerRoutingTrailer extends TrailerRoutingResource {

    private boolean truckIsFixed;
    private TrailerRoutingTruck fixedTruck;

    private int uomCarryingWeight;
    private int uomLiftingWeight;
    private int uomTeuCapacityCount;
    private int uomFeuCapacityCount;
    private boolean sidelifterCapable;
    private boolean overheightOrderCapable;
    private boolean retractableOrderCapable;
    private boolean conventionalOrderCapable;
    private boolean firstOfBdCombinationCapable;
    private boolean requiresSidelifterLicense;
    private boolean dropTrailerCapability;

    public boolean isTruckIsFixed() {
        return truckIsFixed;
    }

    public void setTruckIsFixed(boolean truckIsFixed) {
        this.truckIsFixed = truckIsFixed;
    }

    public TrailerRoutingTruck getFixedTruck() {
        return fixedTruck;
    }

    public void setFixedTruck(TrailerRoutingTruck fixedTruck) {
        this.fixedTruck = fixedTruck;
    }

    public int getUomCarryingWeight() {
        return uomCarryingWeight;
    }

    public void setUomCarryingWeight(int uomCarryingWeight) {
        this.uomCarryingWeight = uomCarryingWeight;
    }

    public int getUomLiftingWeight() {
        return uomLiftingWeight;
    }

    public void setUomLiftingWeight(int uomLiftingWeight) {
        this.uomLiftingWeight = uomLiftingWeight;
    }

    public int getUomTeuCapacityCount() {
        return uomTeuCapacityCount;
    }

    public void setUomTeuCapacityCount(int uomTeuCapacityCount) {
        this.uomTeuCapacityCount = uomTeuCapacityCount;
    }

    public int getUomFeuCapacityCount() {
        return uomFeuCapacityCount;
    }

    public void setUomFeuCapacityCount(int uomFeuCapacityCount) {
        this.uomFeuCapacityCount = uomFeuCapacityCount;
    }

    public boolean isSidelifterCapable() {
        return sidelifterCapable;
    }

    public void setSidelifterCapable(boolean sidelifterCapable) {
        this.sidelifterCapable = sidelifterCapable;
    }

    public boolean isOverheightOrderCapable() {
        return overheightOrderCapable;
    }

    public void setOverheightOrderCapable(boolean overheightOrderCapable) {
        this.overheightOrderCapable = overheightOrderCapable;
    }

    public boolean isRetractableOrderCapable() {
        return retractableOrderCapable;
    }

    public void setRetractableOrderCapable(boolean retractableOrderCapable) {
        this.retractableOrderCapable = retractableOrderCapable;
    }

    public boolean isConventionalOrderCapable() {
        return conventionalOrderCapable;
    }

    public void setConventionalOrderCapable(boolean conventionalOrderCapable) {
        this.conventionalOrderCapable = conventionalOrderCapable;
    }

    public boolean isFirstOfBdCombinationCapable() {
        return firstOfBdCombinationCapable;
    }

    public void setFirstOfBdCombinationCapable(boolean firstOfBdCombinationCapable) {
        this.firstOfBdCombinationCapable = firstOfBdCombinationCapable;
    }

    public boolean isRequiresSidelifterLicense() {
        return requiresSidelifterLicense;
    }

    public void setRequiresSidelifterLicense(boolean requiresSidelifterLicense) {
        this.requiresSidelifterLicense = requiresSidelifterLicense;
    }

    public boolean isDropTrailerCapability() {
        return dropTrailerCapability;
    }

    public void setDropTrailerCapability(boolean dropTrailerCapability) {
        this.dropTrailerCapability = dropTrailerCapability;
    }
}
