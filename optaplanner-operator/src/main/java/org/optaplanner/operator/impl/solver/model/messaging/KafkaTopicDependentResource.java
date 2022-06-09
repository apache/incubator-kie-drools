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
