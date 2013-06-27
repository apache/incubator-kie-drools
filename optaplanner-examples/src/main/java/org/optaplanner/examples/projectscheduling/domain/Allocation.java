/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.examples.projectscheduling.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.solver.CloudComputerStrengthComparator;
import org.optaplanner.examples.cloudbalancing.domain.solver.CloudProcessDifficultyComparator;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity()
@XStreamAlias("PsAllocation")
public class Allocation extends AbstractPersistable {

    private Job job;

    // Planning variables: changes during planning, between score calculations.
    private ExecutionMode executionMode;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @PlanningVariable()
    @ValueRange(type = ValueRangeType.FROM_PLANNING_ENTITY_PROPERTY, planningEntityProperty = "executionModeRange")
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public List<ExecutionMode> getExecutionModeRange() {
        return job.getExecutionModeList();
    }

}
