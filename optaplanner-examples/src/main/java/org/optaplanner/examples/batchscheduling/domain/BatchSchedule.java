/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.batchscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("PipeSchedule")
public class BatchSchedule extends AbstractPersistable {

    private List<Batch> batchList;

    private List<Allocation> allocationList;
    private List<AllocationPath> allocationPathList;

    @XStreamConverter(BendableLongScoreXStreamConverter.class)
    private BendableLongScore score;

    @ProblemFactCollectionProperty
    public List<Batch> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<Batch> batchList) {
        this.batchList = batchList;
    }

    @PlanningEntityCollectionProperty
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    @PlanningEntityCollectionProperty
    public List<AllocationPath> getAllocationPathList() {
        return allocationPathList;
    }

    public void setAllocationPathList(List<AllocationPath> allocationPathList) {
        this.allocationPathList = allocationPathList;
    }

    @PlanningScore(bendableHardLevelsSize = 3, bendableSoftLevelsSize = 2)
    public BendableLongScore getScore() {
        return score;
    }

    public void setScore(BendableLongScore score) {
        this.score = score;
    }

}
