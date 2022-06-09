package org.optaplanner.operator.impl.solver.model;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class DeploymentDependentResource extends CRUKubernetesDependentResource<Deployment, OptaPlannerSolver> {

    private static final String ENV_SOLVER_MESSAGE_IN = "SOLVER_MESSAGE_INPUT";
    private static final String ENV_SOLVER_MESSAGE_OUT = "SOLVER_MESSAGE_OUTPUT";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "KAFKA_BOOTSTRAP_SERVERS";

    public DeploymentDependentResource(KubernetesClient k8s) {
        super(Deployment.class);
        setKubernetesClient(k8s);
    }

    @Override
    protected Deployment desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        String deploymentName = solver.getDeploymentName();

        Container container = new ContainerBuilder()
                .withName(deploymentName)
                .withImage(solver.getSpec().getSolverImage())
                .withEnv(buildEnvironmentVariablesMapping(solver.getConfigMapName()))
                .build();

        DeploymentSpecBuilder deploymentSpecBuilder = new DeploymentSpecBuilder()
                .withNewSelector().withMatchLabels(Map.of("app", deploymentName))
                .endSelector();
        if (!solver.getSpec().getScaling().isDynamic()) {
            // Set deployment replicas only for static scaling, otherwise the operator would interfere with KEDA.
            deploymentSpecBuilder.withReplicas(solver.getSpec().getScaling().getReplicas());
        }
        deploymentSpecBuilder.withNewTemplate()
                .withNewMetadata().withLabels(Map.of("app", deploymentName)).endMetadata()
                .withNewSpec()
                .withContainers(container)
                .endSpec()
                .endTemplate();

        return new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .withNamespace(solver.getNamespace())
                .endMetadata()
                .withSpec(deploymentSpecBuilder.build())
                .build();
    }

    private List<EnvVar> buildEnvironmentVariablesMapping(String configMapName) {
        EnvVar envVarMessageInput = new EnvVarBuilder()
                .withName(ENV_SOLVER_MESSAGE_IN)
                .withNewValueFrom()
                .withNewConfigMapKeyRef(ConfigMapDependentResource.SOLVER_MESSAGE_INPUT_KEY, configMapName, false)
                .endValueFrom()
                .build();

        EnvVar envVarMessageOutput = new EnvVarBuilder()
                .withName(ENV_SOLVER_MESSAGE_OUT)
                .withNewValueFrom()
                .withNewConfigMapKeyRef(ConfigMapDependentResource.SOLVER_MESSAGE_OUTPUT_KEY, configMapName, false)
                .endValueFrom()
                .build();

        EnvVar envVarKafkaServers = new EnvVarBuilder()
                .withName(KAFKA_BOOTSTRAP_SERVERS)
                .withNewValueFrom()
                .withNewConfigMapKeyRef(ConfigMapDependentResource.SOLVER_KAFKA_BOOTSTRAP_SERVERS_KEY, configMapName, false)
                .endValueFrom()
                .build();
        return List.of(envVarMessageInput, envVarMessageOutput, envVarKafkaServers);
    }
}
