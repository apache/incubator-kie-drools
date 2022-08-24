package org.optaplanner.operator.impl.solver.model.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ArtemisQueueSpec {

    private static final String ROUTING_TYPE_ANYCAST = "anycast";

    private String addressName;

    private String queueName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String routingType = ROUTING_TYPE_ANYCAST;

    public String getAddressName() {
        return addressName;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getRoutingType() {
        return routingType;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
