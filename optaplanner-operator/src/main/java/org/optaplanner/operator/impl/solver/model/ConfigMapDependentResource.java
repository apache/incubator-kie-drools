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

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class ConfigMapDependentResource extends CRUKubernetesDependentResource<ConfigMap, OptaPlannerSolver> {

    public static final String SOLVER_MESSAGE_INPUT_KEY = "solver.message.input";
    public static final String SOLVER_MESSAGE_OUTPUT_KEY = "solver.message.output";
    public static final String SOLVER_KAFKA_BOOTSTRAP_SERVERS_KEY = "solver.kafka.bootstrap.servers";

    public ConfigMapDependentResource(KubernetesClient kubernetesClient) {
        super(ConfigMap.class);
        setKubernetesClient(kubernetesClient);
    }

    @Override
    protected ConfigMap desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        Map<String, String> data = new HashMap<>();
        if (solver.getStatus() != null) {
            data.put(SOLVER_MESSAGE_INPUT_KEY, solver.getStatus().getInputMessageAddress());
            data.put(SOLVER_MESSAGE_OUTPUT_KEY, solver.getStatus().getOutputMessageAddress());
        }
        data.put(SOLVER_KAFKA_BOOTSTRAP_SERVERS_KEY, solver.getSpec().getKafkaBootstrapServers());

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
        getKubernetesClient()
                .pods()
                .inNamespace(namespace)
                .withLabel("app", solver.getMetadata().getName())
                .delete();
        return resultingConfigMap;
    }
}
