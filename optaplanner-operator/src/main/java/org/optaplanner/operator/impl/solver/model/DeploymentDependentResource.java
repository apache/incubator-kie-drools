package org.optaplanner.operator.impl.solver.model;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
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

        Container container = new ContainerBuilder()
                .withName(deploymentName)
                .withImage(solver.getSpec().getSolverImage())
                .withEnv(buildEnvironmentVariablesMapping(solver))
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
