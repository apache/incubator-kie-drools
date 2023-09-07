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

package org.optaplanner.operator.impl.solver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.fabric8.kubernetes.api.model.SecretKeySelector;

public final class AmqBroker {

    private static final int DEFAULT_PORT = 5672;

    private static final int DEFAULT_MANAGEMENT_PORT = 8161;

    // TODO: Try to detect.
    private static final String DEFAULT_BROKER_NAME = "amq-broker";

    private String host;

    private int port = DEFAULT_PORT;

    private String managementHost;

    private int managementPort = DEFAULT_MANAGEMENT_PORT;

    private String brokerName = DEFAULT_BROKER_NAME;

    private SecretKeySelector usernameSecretRef;

    private SecretKeySelector passwordSecretRef;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getManagementHost() {
        return managementHost;
    }

    public void setManagementHost(String managementHost) {
        this.managementHost = managementHost;
    }

    public int getManagementPort() {
        return managementPort;
    }

    public void setManagementPort(int managementPort) {
        this.managementPort = managementPort;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public SecretKeySelector getUsernameSecretRef() {
        return usernameSecretRef;
    }

    public void setUsernameSecretRef(SecretKeySelector usernameSecretRef) {
        this.usernameSecretRef = usernameSecretRef;
    }

    public SecretKeySelector getPasswordSecretRef() {
        return passwordSecretRef;
    }

    public void setPasswordSecretRef(SecretKeySelector passwordSecretRef) {
        this.passwordSecretRef = passwordSecretRef;
    }

    @JsonIgnore
    public String getManagementEndpoint() {
        return String.format("%s:%s", managementHost, managementPort);
    }
}
