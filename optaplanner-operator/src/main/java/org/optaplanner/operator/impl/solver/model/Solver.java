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

import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("org.optaplanner.solver")
@Version("v1")
public final class Solver extends CustomResource<SolverSpec, SolverStatus> implements Namespaced {

    // TODO: Move all the following methods away if this class ever becomes an API.
    @JsonIgnore
    public String getNamespace() {
        return getMetadata().getNamespace();
    }

    @JsonIgnore
    public String getConfigMapName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getDeploymentName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getInputMessageAddressName() {
        return getMessageAddressName(MessageAddress.INPUT);
    }

    @JsonIgnore
    public String getOutputMessageAddressName() {
        return getMessageAddressName(MessageAddress.OUTPUT);
    }

    @JsonIgnore
    public String getMessageAddressName(MessageAddress messageAddress) {
        return String.format("%s-%s", getSolverName(), messageAddress.getName());
    }

    @JsonIgnore
    private String getSolverName() {
        return getMetadata().getName();
    }
}
