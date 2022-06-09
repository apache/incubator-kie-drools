package org.optaplanner.operator.impl.solver;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.optaplanner.operator.impl.solver.model.ConfigMapDependentResource;
import org.optaplanner.operator.impl.solver.model.DeploymentDependentResource;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolverStatus;
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
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependentResourceConfig;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.strimzi.api.kafka.model.KafkaTopic;

@ControllerConfiguration
public final class OptaPlannerSolverReconciler implements Reconciler<OptaPlannerSolver>, ErrorStatusHandler<OptaPlannerSolver>,
        EventSourceInitializer<OptaPlannerSolver> {

    private KubernetesClient kubernetesClient;

    private final DeploymentDependentResource deploymentDependentResource;
    private final KafkaTopicDependentResource inputKafkaTopicDependentResource;
    private final KafkaTopicDependentResource outputKafkaTopicDependentResource;
    private final ConfigMapDependentResource configMapDependentResource;

    @Inject
    public OptaPlannerSolverReconciler(KubernetesClient kubernetesClient) {
        deploymentDependentResource = new DeploymentDependentResource(kubernetesClient);
        inputKafkaTopicDependentResource = new KafkaTopicDependentResource(MessageAddress.INPUT, kubernetesClient);
        outputKafkaTopicDependentResource = new KafkaTopicDependentResource(MessageAddress.OUTPUT, kubernetesClient);
        configMapDependentResource = new ConfigMapDependentResource(kubernetesClient);

        // The two dependent resource of the same type need to be differentiated by a label.
        inputKafkaTopicDependentResource.configureWith(
                new KubernetesDependentResourceConfig().setLabelSelector(getTopicSelector(MessageAddress.INPUT)));
        outputKafkaTopicDependentResource.configureWith(
                new KubernetesDependentResourceConfig().setLabelSelector(getTopicSelector(MessageAddress.OUTPUT)));
    }

    private String getTopicSelector(MessageAddress messageAddress) {
        return KafkaTopicDependentResource.MESSAGE_ADDRESS_LABEL + "=" + messageAddress.getName();
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<OptaPlannerSolver> context) {
        return EventSourceInitializer.nameEventSources(deploymentDependentResource.initEventSource(context),
                inputKafkaTopicDependentResource.initEventSource(context),
                outputKafkaTopicDependentResource.initEventSource(context),
                configMapDependentResource.initEventSource(context));
    }

    @Override
    public UpdateControl<OptaPlannerSolver> reconcile(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        deploymentDependentResource.reconcile(solver, context);
        inputKafkaTopicDependentResource.reconcile(solver, context);
        outputKafkaTopicDependentResource.reconcile(solver, context);

        Optional<KafkaTopic> inputKafkaTopic = inputKafkaTopicDependentResource.getSecondaryResource(solver);
        Optional<KafkaTopic> outputKafkaTopic = outputKafkaTopicDependentResource.getSecondaryResource(solver);

        OptaPlannerSolverStatus solverStatus = OptaPlannerSolverStatus.success();
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
    public ErrorStatusUpdateControl<OptaPlannerSolver> updateErrorStatus(OptaPlannerSolver solver,
            Context<OptaPlannerSolver> context, Exception e) {
        solver.setStatus(OptaPlannerSolverStatus.error(e));
        return ErrorStatusUpdateControl.updateStatus(solver);
    }
}
