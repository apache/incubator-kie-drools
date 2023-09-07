/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.operator.impl.solver.model.keda;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.operator.impl.solver.model.common.ResourceNameReference;

public final class ScaledObjectSpec {

    private ResourceNameReference scaleTargetRef;

    private List<Trigger> triggers = new ArrayList<>();

    private int cooldownPeriod;

    private int pollingInterval;

    private int minReplicaCount;

    private int maxReplicaCount;

    public ScaledObjectSpec withTrigger(Trigger trigger) {
        triggers.add(trigger);
        return this;
    }

    public ResourceNameReference getScaleTargetRef() {
        return scaleTargetRef;
    }

    public void setScaleTargetRef(ResourceNameReference scaleTargetRef) {
        this.scaleTargetRef = scaleTargetRef;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public int getCooldownPeriod() {
        return cooldownPeriod;
    }

    public void setCooldownPeriod(int cooldownPeriod) {
        this.cooldownPeriod = cooldownPeriod;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public int getMinReplicaCount() {
        return minReplicaCount;
    }

    public void setMinReplicaCount(int minReplicaCount) {
        this.minReplicaCount = minReplicaCount;
    }

    public int getMaxReplicaCount() {
        return maxReplicaCount;
    }

    public void setMaxReplicaCount(int maxReplicaCount) {
        this.maxReplicaCount = maxReplicaCount;
    }
}
