package org.optaplanner.operator.impl.solver;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.optaplanner.operator.impl.solver.model.ConfigMapDependentResource;
import org.optaplanner.operator.impl.solver.model.DeploymentDependentResource;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolverStatus;
import org.optaplanner.operator.impl.solver.model.keda.ScaledObjectDependentResource;
import org.optaplanner.operator.impl.solver.model.keda.TriggerAuthenticationDependentResource;
import org.optaplanner.operator.impl.solver.model.messaging.ArtemisQueue;
import org.optaplanner.operator.impl.solver.model.messaging.ArtemisQueueDependentResource;
import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusHandler;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;

@ControllerConfiguration(name = "optaplanner-solver")
public final class OptaPlannerSolverReconciler implements Reconciler<OptaPlannerSolver>, ErrorStatusHandler<OptaPlannerSolver>,
        EventSourceInitializer<OptaPlannerSolver> {

    private KubernetesClient kubernetesClient;

    private final DeploymentDependentResource deploymentDependentResource;
    private final ArtemisQueueDependentResource inputQueueDependentResource;
    private final ArtemisQueueDependentResource outputQueueDependentResource;
    private final ConfigMapDependentResource configMapDependentResource;
    private final TriggerAuthenticationDependentResource triggerAuthenticationDependentResource;
    private final ScaledObjectDependentResource scaledObjectDependentResource;

    @Inject
    public OptaPlannerSolverReconciler(KubernetesClient kubernetesClient) {
        deploymentDependentResource = new DeploymentDependentResource(kubernetesClient);
        inputQueueDependentResource = new ArtemisQueueDependentResource(MessageAddress.INPUT, kubernetesClient);
        outputQueueDependentResource = new ArtemisQueueDependentResource(MessageAddress.OUTPUT, kubernetesClient);
        configMapDependentResource = new ConfigMapDependentResource(kubernetesClient);
        triggerAuthenticationDependentResource = new TriggerAuthenticationDependentResource(kubernetesClient);
        scaledObjectDependentResource = new ScaledObjectDependentResource(kubernetesClient);

        inputQueueDependentResource.setResourceDiscriminator(new ResourceIDMatcherDiscriminator<>(
                optaPlannerSolver -> new ResourceID(optaPlannerSolver.getInputMessageAddressName(),
                        optaPlannerSolver.getMetadata().getNamespace())));
        outputQueueDependentResource.setResourceDiscriminator(new ResourceIDMatcherDiscriminator<>(
                optaPlannerSolver -> new ResourceID(optaPlannerSolver.getOutputMessageAddressName(),
                        optaPlannerSolver.getMetadata().getNamespace())));
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<OptaPlannerSolver> context) {
        return EventSourceInitializer.nameEventSources(deploymentDependentResource.initEventSource(context),
                inputQueueDependentResource.initEventSource(context),
                outputQueueDependentResource.initEventSource(context),
                configMapDependentResource.initEventSource(context),
                triggerAuthenticationDependentResource.initEventSource(context),
                scaledObjectDependentResource.initEventSource(context));
    }

    @Override
    public UpdateControl<OptaPlannerSolver> reconcile(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        boolean isReady = true;
        deploymentDependentResource.reconcile(solver, context);
        inputQueueDependentResource.reconcile(solver, context);
        outputQueueDependentResource.reconcile(solver, context);
        if (solver.getSpec().getScaling().isDynamic()) {
            triggerAuthenticationDependentResource.reconcile(solver, context);
            scaledObjectDependentResource.reconcile(solver, context);

            if (scaledObjectDependentResource.getSecondaryResource(solver, context).isEmpty() ||
                    triggerAuthenticationDependentResource.getSecondaryResource(solver, context).isEmpty()) {
                isReady = false;
            }
        }

        Optional<ArtemisQueue> inputQueue = inputQueueDependentResource.getSecondaryResource(solver, context);
        Optional<ArtemisQueue> outputQueue = outputQueueDependentResource.getSecondaryResource(solver, context);
        if (inputQueue.isEmpty() || outputQueue.isEmpty()) {
            isReady = false;
        }

        if (inputQueue.isPresent() && outputQueue.isPresent()) {
            configMapDependentResource.reconcile(solver, context);
            if (configMapDependentResource.getSecondaryResource(solver, context).isEmpty()) {
                isReady = false;
            }
        }

        if (isReady) {
            solver.setStatus(OptaPlannerSolverStatus.ready(solver.getMetadata().getGeneration()));
            solver.getStatus().setInputMessageAddress(inputQueue.get().getSpec().getQueueName());
            solver.getStatus().setOutputMessageAddress(outputQueue.get().getSpec().getQueueName());
        } else {
            solver.setStatus(OptaPlannerSolverStatus.unknown(solver.getMetadata().getGeneration()));
        }
        return UpdateControl.updateStatus(solver);
    }

    @Override
    public ErrorStatusUpdateControl<OptaPlannerSolver> updateErrorStatus(OptaPlannerSolver solver,
            Context<OptaPlannerSolver> context, Exception e) {
        solver.setStatus(OptaPlannerSolverStatus.error(solver.getMetadata().getGeneration(), e));
        return ErrorStatusUpdateControl.updateStatus(solver);
    }
}
