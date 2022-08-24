package org.optaplanner.operator.impl.solver.model.keda;

public final class TriggerMetadata {
    private String managementEndpoint;
    private String queueName;
    private String queueLength;
    private String brokerName;
    private String brokerAddress;

    public String getManagementEndpoint() {
        return managementEndpoint;
    }

    public void setManagementEndpoint(String managementEndpoint) {
        this.managementEndpoint = managementEndpoint;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(String queueLength) {
        this.queueLength = queueLength;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }
}
