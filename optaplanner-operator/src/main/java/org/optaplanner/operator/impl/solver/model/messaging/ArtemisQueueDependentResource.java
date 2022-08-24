package org.optaplanner.operator.impl.solver.model.messaging;

import java.util.Map;

import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class ArtemisQueueDependentResource extends CRUDKubernetesDependentResource<ArtemisQueue, OptaPlannerSolver> {

    public ArtemisQueueDependentResource(MessageAddress messageAddress, KubernetesClient kubernetesClient) {
        super(ArtemisQueue.class);
        this.messageAddress = messageAddress;
        setKubernetesClient(kubernetesClient);
    }

    public static final String MESSAGE_ADDRESS_LABEL = "message-address";

    private final MessageAddress messageAddress;

    @Override
    protected ArtemisQueue desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        final String queueName = solver.getMessageAddressName(messageAddress);

        // The two dependent resource of the same type need to be differentiated by a label.
        Map<String, String> labels = Map.of(MESSAGE_ADDRESS_LABEL, messageAddress.getName());

        ObjectMeta objectMeta = new ObjectMetaBuilder()
                .withName(queueName)
                .withNamespace(solver.getNamespace())
                .withLabels(labels)
                .build();

        ArtemisQueueSpec spec = new ArtemisQueueSpec();
        spec.setAddressName(queueName);
        spec.setQueueName(queueName);

        ArtemisQueue artemisQueue = new ArtemisQueue();
        artemisQueue.setMetadata(objectMeta);
        artemisQueue.setSpec(spec);

        return artemisQueue;
    }
}
