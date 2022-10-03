package org.optaplanner.operator.impl.solver.model;

import io.fabric8.kubernetes.api.model.PodTemplateSpec;

public final class OptaPlannerSolverSpec {
    private AmqBroker amqBroker;
    private Scaling scaling = new Scaling();
    private PodTemplateSpec template;

    public Scaling getScaling() {
        return scaling;
    }

    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }

    public AmqBroker getAmqBroker() {
        return amqBroker;
    }

    public void setAmqBroker(AmqBroker amqBroker) {
        this.amqBroker = amqBroker;
    }

    public PodTemplateSpec getTemplate() {
        return template;
    }

    public void setTemplate(PodTemplateSpec template) {
        this.template = template;
    }
}
