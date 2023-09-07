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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class DeploymentDependentResource extends CRUDKubernetesDependentResource<Deployment, OptaPlannerSolver> {

    private static final String ENV_SOLVER_MESSAGE_IN = "SOLVER_MESSAGE_INPUT";
    private static final String ENV_SOLVER_MESSAGE_OUT = "SOLVER_MESSAGE_OUTPUT";
    private static final String ENV_AMQ_HOST = "SOLVER_MESSAGE_AMQ_HOST";
    private static final String ENV_AMQ_PORT = "SOLVER_MESSAGE_AMQ_PORT";
    private static final String ENV_AMQ_USERNAME = "SOLVER_MESSAGE_AMQ_USERNAME";
    private static final String ENV_AMQ_PASSWORD = "SOLVER_MESSAGE_AMQ_PASSWORD";

    public DeploymentDependentResource(KubernetesClient k8s) {
        super(Deployment.class);
        setKubernetesClient(k8s);
    }

    @Override
    protected Deployment desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        String deploymentName = solver.getDeploymentName();

        DeploymentSpecBuilder deploymentSpecBuilder = new DeploymentSpecBuilder()
                .withNewSelector().withMatchLabels(Map.of("app", deploymentName))
                .endSelector();
        if (!solver.getSpec().getScaling().isDynamic()) {
            // Set deployment replicas only for static scaling, otherwise the operator would interfere with KEDA.
            deploymentSpecBuilder.withReplicas(solver.getSpec().getScaling().getReplicas());
        }

        PodTemplateSpec podTemplateSpec = solver.getSpec().getTemplate();
        if (podTemplateSpec == null) {
            throw new IllegalStateException("Solver (" + solver.getMetadata().getName() + ") pod template is missing."
                    + "\nMaybe check the related " + solver.getFullResourceName() + " resource.");
        }

        PodTemplateSpec updatedPodTemplateSpec = addPodAppLabel(podTemplateSpec, deploymentName);
        if (updatedPodTemplateSpec.getSpec() == null || updatedPodTemplateSpec.getSpec().getContainers() == null
                || updatedPodTemplateSpec.getSpec().getContainers().isEmpty()) {
            throw new IllegalStateException("Solver (" + solver.getMetadata().getName()
                    + ") pod template does not contain any container."
                    + "\nMaybe check the related " + solver.getFullResourceName() + " resource.");
        }
        List<Container> containers = updatedPodTemplateSpec.getSpec().getContainers();
        // There may be multiple containers; add the environment variables to all of them.
        for (Container container : containers) {
            if (container.getEnv() == null) {
                container.setEnv(new ArrayList<>());
            }
            container.getEnv().addAll(buildEnvironmentVariablesMapping(solver));
        }

        deploymentSpecBuilder.withTemplate(updatedPodTemplateSpec);
        return new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .withNamespace(solver.getNamespace())
                .endMetadata()
                .withSpec(deploymentSpecBuilder.build())
                .build();
    }

    private PodTemplateSpec addPodAppLabel(PodTemplateSpec podTemplateSpec, String deploymentName) {
        return new PodTemplateSpecBuilder(podTemplateSpec)
                .editOrNewMetadata()
                .addToLabels("app", deploymentName)
                .endMetadata()
                .build();
    }

    private List<EnvVar> buildEnvironmentVariablesMapping(OptaPlannerSolver solver) {
        String configMapName = solver.getConfigMapName();
        EnvVar envVarMessageInput = buildEnvVarFromConfigMap(ENV_SOLVER_MESSAGE_IN, configMapName,
                ConfigMapDependentResource.SOLVER_MESSAGE_INPUT_KEY);

        EnvVar envVarMessageOutput = buildEnvVarFromConfigMap(ENV_SOLVER_MESSAGE_OUT, configMapName,
                ConfigMapDependentResource.SOLVER_MESSAGE_OUTPUT_KEY);

        EnvVar envVarAmqHost = buildEnvVarFromConfigMap(ENV_AMQ_HOST, configMapName,
                ConfigMapDependentResource.SOLVER_MESSAGE_AMQ_HOST_KEY);

        EnvVar envVarAmqPort = buildEnvVarFromConfigMap(ENV_AMQ_PORT, configMapName,
                ConfigMapDependentResource.SOLVER_MESSAGE_AMQ_PORT_KEY);

        EnvVar envVarAmqUsername = buildEnvVarFromSecretKeySelector(ENV_AMQ_USERNAME,
                solver.getSpec().getAmqBroker().getUsernameSecretRef());

        EnvVar envVarAmqPassword = buildEnvVarFromSecretKeySelector(ENV_AMQ_PASSWORD,
                solver.getSpec().getAmqBroker().getPasswordSecretRef());

        return List.of(envVarMessageInput, envVarMessageOutput, envVarAmqHost, envVarAmqPort, envVarAmqUsername,
                envVarAmqPassword);
    }

    private EnvVar buildEnvVarFromConfigMap(String envVariable, String configMapName, String configMapKey) {
        return new EnvVarBuilder()
                .withName(envVariable)
                .withNewValueFrom()
                .withNewConfigMapKeyRef(configMapKey, configMapName, false)
                .endValueFrom()
                .build();
    }

    private EnvVar buildEnvVarFromSecretKeySelector(String envVariable, SecretKeySelector secretKeySelector) {
        return new EnvVarBuilder()
                .withName(envVariable)
                .withNewValueFrom()
                .withNewSecretKeyRef(secretKeySelector.getKey(), secretKeySelector.getName(), secretKeySelector.getOptional())
                .endValueFrom()
                .build();
    }
}
