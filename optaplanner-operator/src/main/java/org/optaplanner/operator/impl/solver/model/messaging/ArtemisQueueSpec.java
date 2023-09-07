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
