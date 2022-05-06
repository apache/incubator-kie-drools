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

package org.optaplanner.operator.impl.solver;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.optaplanner.operator.impl.solver.model.ConfigMapDependentResource;
import org.optaplanner.operator.impl.solver.model.DeploymentDependentResource;
import org.optaplanner.operator.impl.solver.model.Solver;
import org.optaplanner.operator.impl.solver.model.SolverStatus;
import org.optaplanner.operator.impl.solver.model.messaging.KafkaTopicDependentResource;
import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusHandler;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.strimzi.api.kafka.model.KafkaTopic;

@ControllerConfiguration
public final class SolverReconciler implements Reconciler<Solver>, ErrorStatusHandler<Solver>, EventSourceInitializer<Solver> {

    private KubernetesClient kubernetesClient;

    private final DeploymentDependentResource deploymentDependentResource;
    private final KafkaTopicDependentResource inputKafkaTopicDependentResource;
    private final KafkaTopicDependentResource outputKafkaTopicDependentResource;
    private final ConfigMapDependentResource configMapDependentResource;

    @Inject
    public SolverReconciler(KubernetesClient kubernetesClient) {
        deploymentDependentResource = new DeploymentDependentResource(kubernetesClient);
        inputKafkaTopicDependentResource = new KafkaTopicDependentResource(MessageAddress.INPUT, kubernetesClient);
        outputKafkaTopicDependentResource = new KafkaTopicDependentResource(MessageAddress.OUTPUT, kubernetesClient);
        configMapDependentResource = new ConfigMapDependentResource(kubernetesClient);
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<Solver> context) {
        return EventSourceInitializer.nameEventSources(deploymentDependentResource.initEventSource(context),
                inputKafkaTopicDependentResource.initEventSource(context),
                outputKafkaTopicDependentResource.initEventSource(context),
                configMapDependentResource.initEventSource(context));
    }

    @Override
    public UpdateControl<Solver> reconcile(Solver solver, Context<Solver> context) {
        deploymentDependentResource.reconcile(solver, context);
        inputKafkaTopicDependentResource.reconcile(solver, context);
        outputKafkaTopicDependentResource.reconcile(solver, context);

        Optional<KafkaTopic> inputKafkaTopic = inputKafkaTopicDependentResource.getSecondaryResource(solver);
        Optional<KafkaTopic> outputKafkaTopic = outputKafkaTopicDependentResource.getSecondaryResource(solver);

        SolverStatus solverStatus = SolverStatus.success();
        solver.setStatus(solverStatus);
        if (inputKafkaTopic.isPresent()) {
            solverStatus.setInputMessageAddress(inputKafkaTopic.get().getSpec().getTopicName());
        }
        if (outputKafkaTopic.isPresent()) {
            solverStatus.setOutputMessageAddress(outputKafkaTopic.get().getSpec().getTopicName());
        }

        if (inputKafkaTopic.isPresent() && outputKafkaTopic.isPresent()) {
            configMapDependentResource.reconcile(solver, context);
        }
        return UpdateControl.updateStatus(solver);
    }

    @Override
    public ErrorStatusUpdateControl<Solver> updateErrorStatus(Solver solver, Context<Solver> context, Exception e) {
        solver.setStatus(SolverStatus.error(e));
        return ErrorStatusUpdateControl.updateStatus(solver);
    }
}
