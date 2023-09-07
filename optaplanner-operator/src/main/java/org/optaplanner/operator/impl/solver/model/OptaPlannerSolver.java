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

import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@Group(OptaPlannerSolver.GROUP)
@Plural(OptaPlannerSolver.PLURAL)
@Singular(OptaPlannerSolver.SINGULAR)
@Version(OptaPlannerSolver.API_VERSION)
@Kind(OptaPlannerSolver.KIND)
public final class OptaPlannerSolver extends CustomResource<OptaPlannerSolverSpec, OptaPlannerSolverStatus>
        implements Namespaced {
    public static final String GROUP = "org.optaplanner.solver";
    public static final String PLURAL = "solvers";
    public static final String SINGULAR = "solver";
    public static final String API_VERSION = "v1alpha1";
    public static final String KIND = "Solver";

    @Override
    protected OptaPlannerSolverStatus initStatus() {
        return new OptaPlannerSolverStatus();
    }

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
    public String getTriggerAuthenticationName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getScaledObjectName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getScaledObjectTriggerName() {
        return getSolverName();
    }

    @JsonIgnore
    private String getSolverName() {
        return getMetadata().getName();
    }

}
