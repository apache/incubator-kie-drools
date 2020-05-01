/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtTask")
public class Task extends AbstractPersistable {

    private long powerConsumptionMicros;
    private int duration;
    private int startPeriodRangeFrom; // Inclusive
    private int startPeriodRangeTo; // Exclusive

    // Order is equal to resourceList so Resource.getIndex() can be used for the index
    private List<TaskRequirement> taskRequirementList;

    public long getPowerConsumptionMicros() {
        return powerConsumptionMicros;
    }

    public void setPowerConsumptionMicros(long powerConsumptionMicros) {
        this.powerConsumptionMicros = powerConsumptionMicros;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStartPeriodRangeFrom() {
        return startPeriodRangeFrom;
    }

    public void setStartPeriodRangeFrom(int startPeriodRangeFrom) {
        this.startPeriodRangeFrom = startPeriodRangeFrom;
    }

    public int getStartPeriodRangeTo() {
        return startPeriodRangeTo;
    }

    public void setStartPeriodRangeTo(int startPeriodRangeTo) {
        this.startPeriodRangeTo = startPeriodRangeTo;
    }

    public List<TaskRequirement> getTaskRequirementList() {
        return taskRequirementList;
    }

    public void setTaskRequirementList(List<TaskRequirement> taskRequirementList) {
        this.taskRequirementList = taskRequirementList;
    }

    public int getUsage(Resource resource) {
        return taskRequirementList.get(resource.getIndex()).getResourceUsage();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getResourceUsageMultiplicand() {
        int multiplicand = 1;
        for (TaskRequirement taskRequirement : taskRequirementList) {
            multiplicand *= taskRequirement.getResourceUsage();
        }
        return multiplicand;
    }

    public String getLabel() {
        return "Task " + id;
    }

}
