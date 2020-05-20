/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.domain;

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningSolution
public class RockTourSolution extends AbstractPersistable {

    private String tourName;

    @ConstraintConfigurationProvider
    private RockTourConstraintConfiguration constraintConfiguration;

    @ProblemFactProperty
    private RockBus bus;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "showRange")
    private List<RockShow> showList;

    @PlanningScore
    private HardMediumSoftLongScore score = null;

    public RockTourSolution() {
    }

    public RockTourSolution(long id) {
        super(id);
    }

    @ValueRangeProvider(id = "busRange")
    public List<RockBus> getBugRange() {
        return Collections.singletonList(bus);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public RockTourConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(RockTourConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    public RockBus getBus() {
        return bus;
    }

    public void setBus(RockBus bus) {
        this.bus = bus;
    }

    public List<RockShow> getShowList() {
        return showList;
    }

    public void setShowList(List<RockShow> showList) {
        this.showList = showList;
    }

    public HardMediumSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftLongScore score) {
        this.score = score;
    }

}
