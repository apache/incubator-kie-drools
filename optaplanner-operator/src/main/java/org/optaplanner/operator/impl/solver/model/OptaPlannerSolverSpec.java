package org.optaplanner.operator.impl.solver.model;

public final class OptaPlannerSolverSpec {
    private String solverImage;
    private String kafkaBootstrapServers;
    private String kafkaCluster;

    private Scaling scaling;

    public OptaPlannerSolverSpec() {
        // required by Jackson
    }

    public OptaPlannerSolverSpec(String solverImage, String kafkaBootstrapServers, String kafkaCluster) {
        this.solverImage = solverImage;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaCluster = kafkaCluster;
    }

    public String getSolverImage() {
        return solverImage;
    }

    public void setSolverImage(String solverImage) {
        this.solverImage = solverImage;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaCluster() {
        return kafkaCluster;
    }

    public void setKafkaCluster(String kafkaCluster) {
        this.kafkaCluster = kafkaCluster;
    }

    public Scaling getScaling() {
        return scaling;
    }

    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }
}
