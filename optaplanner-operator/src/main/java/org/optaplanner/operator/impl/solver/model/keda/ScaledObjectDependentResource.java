package org.optaplanner.operator.impl.solver.model.keda;

import org.optaplanner.operator.impl.solver.model.AmqBroker;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;
import org.optaplanner.operator.impl.solver.model.common.ResourceNameReference;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

/*
Example YAML:
-----------------------------------------
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: school-timetabling-scaledobject
  namespace: artemis
spec:
  scaleTargetRef:
    name: school-timetabling-activemq
  triggers:
    - type: artemis-queue
      name: school-timetabling-trigger
      metadata:
        managementEndpoint: "ex-aao-hdls-svc.artemis.svc.cluster.local:8161"
        queueName: "school-timetabling-problem"
        queueLength: "1"
        brokerName: "amq-broker"
        brokerAddress: "school-timetabling-problem"
      authenticationRef:
        name: school-timetabling-scaledobject-auth
  cooldownPeriod: 10
  maxReplicaCount: 3
  minReplicaCount: 0
  pollingInterval: 10
*/

@KubernetesDependent
public final class ScaledObjectDependentResource extends CRUKubernetesDependentResource<ScaledObject, OptaPlannerSolver> {

    public static final String ARTEMIS_QUEUE_TRIGGER = "artemis-queue";

    /**
     * Required to scale down to zero pods.
     */
    private static final int MIN_REPLICAS = 0;
    private static final int POLLING_INTERVAL = 10;
    private static final int COOLDOWN_PERIOD = 10;

    /**
     * The scaler increases replicas if the queue message count is greater than this value per active replica.
     */
    private static final int TARGET_QUEUE_LENGTH = 1;

    public ScaledObjectDependentResource(KubernetesClient kubernetesClient) {
        super(ScaledObject.class);
        setKubernetesClient(kubernetesClient);
    }

    @Override
    protected ScaledObject desired(OptaPlannerSolver optaPlannerSolver, Context<OptaPlannerSolver> context) {
        AmqBroker amqBroker = optaPlannerSolver.getSpec().getAmqBroker();

        TriggerMetadata triggerMetadata = new TriggerMetadata();
        triggerMetadata.setBrokerAddress(optaPlannerSolver.getInputMessageAddressName());
        triggerMetadata.setQueueName(optaPlannerSolver.getInputMessageAddressName());
        triggerMetadata.setQueueLength(String.valueOf(TARGET_QUEUE_LENGTH));
        triggerMetadata.setBrokerName(amqBroker.getBrokerName());
        triggerMetadata.setManagementEndpoint(amqBroker.getManagementEndpoint());

        Trigger trigger = new Trigger();
        trigger.setType(ARTEMIS_QUEUE_TRIGGER);
        trigger.setMetadata(triggerMetadata);
        trigger.setName(optaPlannerSolver.getScaledObjectTriggerName());
        trigger.setAuthenticationRef(new ResourceNameReference(optaPlannerSolver.getTriggerAuthenticationName()));

        ScaledObjectSpec spec = new ScaledObjectSpec();
        spec.withTrigger(trigger);
        spec.setScaleTargetRef(new ResourceNameReference(optaPlannerSolver.getDeploymentName()));
        spec.setMinReplicaCount(MIN_REPLICAS);
        spec.setMaxReplicaCount(optaPlannerSolver.getSpec().getScaling().getReplicas());
        spec.setPollingInterval(POLLING_INTERVAL);
        spec.setCooldownPeriod(COOLDOWN_PERIOD);

        ScaledObject scaledObject = new ScaledObject();
        scaledObject.setSpec(spec);
        scaledObject.setMetadata(buildMetadata(optaPlannerSolver));
        scaledObject.setStatus(new ScaledObject.ScaledObjectStatus());

        return scaledObject;
    }

    private ObjectMeta buildMetadata(OptaPlannerSolver optaPlannerSolver) {
        return new ObjectMetaBuilder()
                .withName(optaPlannerSolver.getScaledObjectName())
                .withNamespace(optaPlannerSolver.getNamespace())
                .build();
    }
}
