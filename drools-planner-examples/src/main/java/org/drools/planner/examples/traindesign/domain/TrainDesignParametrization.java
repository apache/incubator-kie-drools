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

@XStreamAlias("TrainDesignParametrization")
public class TrainDesignParametrization extends AbstractPersistable {

    private int crewImbalancePenalty;
    private int trainImbalancePenalty;
    private int trainTravelCostPerDistance; // per 1000 miles (to avoid Double rounding errors and BigDecimal)
    private int carTravelCostPerDistance; // per 1000 miles (to avoid Double rounding errors and BigDecimal)
    private int workEventCost;
    private int maximumBlocksPerTrain;
    private int maximumBlockSwapsPerBlock;
    private int maximumIntermediateWorkEventsPerTrain;
    private int trainStartCost;
    private int missedCarCost;

    public int getCrewImbalancePenalty() {
        return crewImbalancePenalty;
    }

    public void setCrewImbalancePenalty(int crewImbalancePenalty) {
        this.crewImbalancePenalty = crewImbalancePenalty;
    }

    public int getTrainImbalancePenalty() {
        return trainImbalancePenalty;
    }

    public void setTrainImbalancePenalty(int trainImbalancePenalty) {
        this.trainImbalancePenalty = trainImbalancePenalty;
    }

    public int getTrainTravelCostPerDistance() {
        return trainTravelCostPerDistance;
    }

    public void setTrainTravelCostPerDistance(int trainTravelCostPerDistance) {
        this.trainTravelCostPerDistance = trainTravelCostPerDistance;
    }

    public int getCarTravelCostPerDistance() {
        return carTravelCostPerDistance;
    }

    public void setCarTravelCostPerDistance(int carTravelCostPerDistance) {
        this.carTravelCostPerDistance = carTravelCostPerDistance;
    }

    public int getWorkEventCost() {
        return workEventCost;
    }

    public void setWorkEventCost(int workEventCost) {
        this.workEventCost = workEventCost;
    }

    public int getMaximumBlocksPerTrain() {
        return maximumBlocksPerTrain;
    }

    public void setMaximumBlocksPerTrain(int maximumBlocksPerTrain) {
        this.maximumBlocksPerTrain = maximumBlocksPerTrain;
    }

    public int getMaximumBlockSwapsPerBlock() {
        return maximumBlockSwapsPerBlock;
    }

    public void setMaximumBlockSwapsPerBlock(int maximumBlockSwapsPerBlock) {
        this.maximumBlockSwapsPerBlock = maximumBlockSwapsPerBlock;
    }

    public int getMaximumIntermediateWorkEventsPerTrain() {
        return maximumIntermediateWorkEventsPerTrain;
    }

    public void setMaximumIntermediateWorkEventsPerTrain(int maximumIntermediateWorkEventsPerTrain) {
        this.maximumIntermediateWorkEventsPerTrain = maximumIntermediateWorkEventsPerTrain;
    }

    public int getTrainStartCost() {
        return trainStartCost;
    }

    public void setTrainStartCost(int trainStartCost) {
        this.trainStartCost = trainStartCost;
    }

    public int getMissedCarCost() {
        return missedCarCost;
    }

    public void setMissedCarCost(int missedCarCost) {
        this.missedCarCost = missedCarCost;
    }

}
