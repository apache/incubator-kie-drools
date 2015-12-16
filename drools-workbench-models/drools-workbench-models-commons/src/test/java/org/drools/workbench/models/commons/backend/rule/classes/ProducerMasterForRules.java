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

import java.util.ArrayList;
import java.util.List;

public class ProducerMasterForRules {

    private String _id;

    private float distance;

    private List<RuleFactor> sortFactors = new ArrayList<RuleFactor>();

    private List<RuleFactor> priceFactors = new ArrayList<RuleFactor>();

    private int sortScore;

    private int priceScore;

    private String primaryCuisine;

    private String secondaryCuisine;

    public String get_id() {
        return _id;
    }

    public void set_id( String _id ) {
        this._id = _id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance( float distance ) {
        this.distance = distance;
    }

    public List<RuleFactor> getSortFactors() {
        return sortFactors;
    }

    public void setSortFactors( List<RuleFactor> sortFactors ) {
        this.sortFactors = sortFactors;
    }

    public List<RuleFactor> getPriceFactors() {
        return priceFactors;
    }

    public void setPriceFactors( List<RuleFactor> pricFactors ) {
        this.priceFactors = pricFactors;
    }

    public int getSortScore() {
        return sortScore;
    }

    public void setSortScore( int sortScore ) {
        this.sortScore = sortScore;
    }

    public int getPriceScore() {
        return priceScore;
    }

    public void setPriceScore( int priceScore ) {
        this.priceScore = priceScore;
    }

    public String getPrimaryCuisine() {
        return primaryCuisine;
    }

    public void setPrimaryCuisine( String primaryCuisine ) {
        this.primaryCuisine = primaryCuisine;
    }

    public String getSecondaryCuisine() {
        return secondaryCuisine;
    }

    public void setSecondaryCuisine( String secondaryCuisine ) {
        this.secondaryCuisine = secondaryCuisine;
    }
}