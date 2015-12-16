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

package org.drools.workbench.models.commons.backend.rule.classes;

public class SearchContext {

    private int partySize;

    private int currentFDMDemand;

    private int currentProducerDemand;

    private int couponRedeemedInLastThirtyDays;

    private boolean fdmPeakHour;

    private boolean producerPeakHour;

    private String serviceStyle;

    // Populated only if last three eateries are same
    private String lastThreeProducerId;

    // Populated only if last three eatery type are same
    private String lastThreeEateryType;

    // Populated only if last three cuisines are same
    private String lastThreeCuisines;

    private String partySizeVSCCap;

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize( int partySize ) {
        this.partySize = partySize;
    }

    public int getCurrentFDMDemand() {
        return currentFDMDemand;
    }

    public void setCurrentFDMDemand( int currentFDMDemand ) {
        this.currentFDMDemand = currentFDMDemand;
    }

    public int getCurrentProducerDemand() {
        return currentProducerDemand;
    }

    public void setCurrentProducerDemand( int currentProducerDemand ) {
        this.currentProducerDemand = currentProducerDemand;
    }

    public int getCouponRedeemedInLastThirtyDays() {
        return couponRedeemedInLastThirtyDays;
    }

    public void setCouponRedeemedInLastThirtyDays( int couponRedeemedInLastThirtyDays ) {
        this.couponRedeemedInLastThirtyDays = couponRedeemedInLastThirtyDays;
    }

    public boolean isFdmPeakHour() {
        return fdmPeakHour;
    }

    public void setFdmPeakHour( boolean fdmPeakHour ) {
        this.fdmPeakHour = fdmPeakHour;
    }

    public boolean isProducerPeakHour() {
        return producerPeakHour;
    }

    public void setProducerPeakHour( boolean producerPeakHour ) {
        this.producerPeakHour = producerPeakHour;
    }

    public String getServiceStyle() {
        return serviceStyle;
    }

    public void setServiceStyle( String serviceStyle ) {
        this.serviceStyle = serviceStyle;
    }

    public String getLastThreeProducerId() {
        return lastThreeProducerId;
    }

    public void setLastThreeProducerId( String lastThreeProducerId ) {
        this.lastThreeProducerId = lastThreeProducerId;
    }

    public String getLastThreeEateryType() {
        return lastThreeEateryType;
    }

    public void setLastThreeEateryType( String lastThreeEateryType ) {
        this.lastThreeEateryType = lastThreeEateryType;
    }

    public String getLastThreeCuisines() {
        return lastThreeCuisines;
    }

    public void setLastThreeCuisines( String lastThreeCuisines ) {
        this.lastThreeCuisines = lastThreeCuisines;
    }

    public String getPartySizeVSCCap() {
        return partySizeVSCCap;
    }

    public void setPartySizeVSCCap( String partySizeVSCCap ) {
        this.partySizeVSCCap = partySizeVSCCap;
    }

}