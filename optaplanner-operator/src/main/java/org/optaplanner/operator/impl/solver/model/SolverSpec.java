/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.operator.impl.solver.model;

public final class SolverSpec {
    private String solverImage;
    private String kafkaBootstrapServers;
    private String kafkaCluster;
    private int replicas = 1;

    public SolverSpec() {
        // required by Jackson
    }

    public SolverSpec(String solverImage, String kafkaBootstrapServers, String kafkaCluster) {
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

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
