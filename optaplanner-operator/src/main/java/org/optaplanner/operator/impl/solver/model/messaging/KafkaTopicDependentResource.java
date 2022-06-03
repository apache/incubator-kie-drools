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

package org.optaplanner.operator.impl.solver.model.messaging;

import java.util.Map;

import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.strimzi.api.kafka.model.KafkaTopic;
import io.strimzi.api.kafka.model.KafkaTopicBuilder;
import io.strimzi.api.kafka.model.KafkaTopicSpecBuilder;

@KubernetesDependent
public final class KafkaTopicDependentResource extends CRUKubernetesDependentResource<KafkaTopic, OptaPlannerSolver> {

    public static final String MESSAGE_ADDRESS_LABEL = "message-address";

    private static final String STRIMZI_LABEL = "strimzi.io/cluster";

    private final MessageAddress messageAddress;

    public KafkaTopicDependentResource(MessageAddress messageAddress, KubernetesClient kubernetesClient) {
        super(KafkaTopic.class);
        this.messageAddress = messageAddress;
        setKubernetesClient(kubernetesClient);
    }

    @Override
    protected KafkaTopic desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        final String topicName = solver.getMessageAddressName(messageAddress);
        KafkaTopicSpecBuilder kafkaTopicSpecBuilder = new KafkaTopicSpecBuilder()
                .withTopicName(topicName);
        if (messageAddress == MessageAddress.INPUT) {
            kafkaTopicSpecBuilder.withPartitions(solver.getSpec().getScaling().getReplicas());
        }
        // The two dependent resource of the same type need to be differentiated by a label.
        Map<String, String> labels = Map.of(MESSAGE_ADDRESS_LABEL, messageAddress.getName());
        return new KafkaTopicBuilder()
                .withNewMetadata()
                .withLabels(labels)
                .withName(topicName)
                .withNamespace(solver.getNamespace())
                .withLabels(Map.of(STRIMZI_LABEL, solver.getSpec().getKafkaCluster()))
                .endMetadata()
                .withSpec(kafkaTopicSpecBuilder.build())
                .build();
    }
}
