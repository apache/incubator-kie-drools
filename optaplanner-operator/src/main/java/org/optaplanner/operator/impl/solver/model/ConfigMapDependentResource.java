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

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class ConfigMapDependentResource extends CRUDKubernetesDependentResource<ConfigMap, OptaPlannerSolver> {

    public static final String SOLVER_MESSAGE_INPUT_KEY = "solver.message.input";
    public static final String SOLVER_MESSAGE_OUTPUT_KEY = "solver.message.output";
    public static final String SOLVER_MESSAGE_AMQ_HOST_KEY = "solver.amq.host";
    public static final String SOLVER_MESSAGE_AMQ_PORT_KEY = "solver.amq.port";

    public ConfigMapDependentResource() {
        super(ConfigMap.class);
    }

    @Override
    protected ConfigMap desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        Map<String, String> data = new HashMap<>();
        data.put(SOLVER_MESSAGE_INPUT_KEY, solver.getInputMessageAddressName());
        data.put(SOLVER_MESSAGE_OUTPUT_KEY, solver.getOutputMessageAddressName());
        data.put(SOLVER_MESSAGE_AMQ_HOST_KEY, solver.getSpec().getAmqBroker().getHost());
        data.put(SOLVER_MESSAGE_AMQ_PORT_KEY, String.valueOf(solver.getSpec().getAmqBroker().getPort()));

        return new ConfigMapBuilder()
                .withNewMetadata()
                .withName(solver.getConfigMapName())
                .withNamespace(solver.getNamespace())
                .endMetadata()
                .withData(data)
                .build();
    }

    @Override
    public ConfigMap update(ConfigMap actual, ConfigMap target, OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        ConfigMap resultingConfigMap = super.update(actual, target, solver, context);
        String namespace = actual.getMetadata().getNamespace();
        context.getClient()
                .pods()
                .inNamespace(namespace)
                .withLabel("app", solver.getMetadata().getName())
                .delete();
        return resultingConfigMap;
    }
}
