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

package org.drools.planner.examples.cloudbalancing.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.annotations.PlanningValueProperty;
import org.drools.planner.core.annotations.PlanningVariableClass;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@PlanningVariableClass // NOTE: DO NOT USE THIS YET
@XStreamAlias("CloudAssignment")
public class CloudAssignment extends AbstractPersistable implements Comparable<CloudAssignment> {

    private CloudProcess cloudProcess;

    // Changed by moves, between score calculations.
    private CloudComputer cloudComputer;

    public CloudProcess getCloudProcess() {
        return cloudProcess;
    }

    public void setCloudProcess(CloudProcess cloudProcess) {
        this.cloudProcess = cloudProcess;
    }

    public CloudComputer getCloudComputer() {
        return cloudComputer;
    }

    @PlanningValueProperty // TODO move to getter NOTE: DO NOT USE THIS YET
    public void setCloudComputer(CloudComputer cloudComputer) {
        this.cloudComputer = cloudComputer;
    }

    public String getLabel() {
        return cloudProcess.getLabel();
    }

    public int getMinimalCpuPower() {
        return cloudProcess.getMinimalCpuPower();
    }

    public int getMinimalMemory() {
        return cloudProcess.getMinimalMemory();
    }

    public int getMinimalNetworkBandwidth() {
        return cloudProcess.getMinimalNetworkBandwidth();
    }

    public int compareTo(CloudAssignment other) {
        return new CompareToBuilder()
                .append(cloudProcess, other.cloudProcess)
                .append(cloudComputer, other.cloudComputer)
                .toComparison();
    }

    public CloudAssignment clone() {
        CloudAssignment clone = new CloudAssignment();
        clone.id = id;
        clone.cloudProcess = cloudProcess;
        clone.cloudComputer = cloudComputer;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudAssignment) {
            CloudAssignment other = (CloudAssignment) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(cloudProcess, other.cloudProcess)
                    .append(cloudComputer, other.cloudComputer)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(cloudProcess)
                .append(cloudComputer)
                .toHashCode();
    }

    @Override
    public String toString() {
        return cloudProcess + "->" + cloudComputer;
    }

}
