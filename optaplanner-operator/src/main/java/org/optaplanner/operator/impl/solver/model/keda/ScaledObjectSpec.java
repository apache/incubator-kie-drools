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
