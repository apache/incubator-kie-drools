package org.optaplanner.operator.impl.solver;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.operator.impl.solver.model.AmqBroker;
import org.optaplanner.operator.impl.solver.model.ConfigMapDependentResource;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolverSpec;
import org.optaplanner.operator.impl.solver.model.Scaling;
import org.optaplanner.operator.impl.solver.model.keda.ScaledObject;
import org.optaplanner.operator.impl.solver.model.keda.ScaledObjectDependentResource;
import org.optaplanner.operator.impl.solver.model.keda.SecretTargetRef;
import org.optaplanner.operator.impl.solver.model.keda.Trigger;
import org.optaplanner.operator.impl.solver.model.keda.TriggerAuthentication;
import org.optaplanner.operator.impl.solver.model.keda.TriggerAuthenticationDependentResource;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OptaPlannerSolverReconcilerTest extends AbstractKubernetesTest {

    @Inject
    private Operator operator;

    // TODO: Replace with @BeforeEach after https://github.com/quarkiverse/quarkus-operator-sdk/issues/388 is resolved.
    public void onStart(@Observes StartupEvent startupEvent) {
        operator.start();
    }

    private String namespace;

    @BeforeEach
    public void createNamespace() {
        namespace = "test-" + UUID.randomUUID();
    }

    @Test
    void createMandatoryDependentResources() {
        final OptaPlannerSolver solver = new OptaPlannerSolver();
        final String solverName = "test-solver";

        AmqBroker amqBroker = createAmqBroker();

        solver.getMetadata().setName(solverName);
        solver.getMetadata().setNamespace(namespace);
        solver.setSpec(new OptaPlannerSolverSpec());
        solver.getSpec().setSolverImage("solver-project-image");
        solver.getSpec().setAmqBroker(amqBroker);
        solver.getSpec().setScaling(new Scaling());
        getClient().resources(OptaPlannerSolver.class).create(solver);

        final String expectedMessageAddressIn = solver.getInputMessageAddressName();
        final String expectedMessageAddressOut = solver.getOutputMessageAddressName();

        await().ignoreException(NullPointerException.class).atMost(1, MINUTES).untilAsserted(() -> {
            OptaPlannerSolver updatedSolver = getClient()
                    .resources(OptaPlannerSolver.class)
                    .inNamespace(solver.getMetadata().getNamespace())
                    .withName(solver.getMetadata().getName())
                    .get();
            assertThat(updatedSolver.getStatus()).isNotNull();
            assertThat(updatedSolver.getStatus().getInputMessageAddress())
                    .isEqualTo(expectedMessageAddressIn);
            assertThat(updatedSolver.getStatus().getOutputMessageAddress())
                    .isEqualTo(expectedMessageAddressOut);
        });

        ConfigMap configMap = getClient()
                .resources(ConfigMap.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .withName(solver.getConfigMapName())
                .get();
        Map<String, String> configMapData = configMap.getData();
        assertThat(configMapData.get(ConfigMapDependentResource.SOLVER_MESSAGE_INPUT_KEY))
                .isEqualTo(expectedMessageAddressIn);
        assertThat(configMapData.get(ConfigMapDependentResource.SOLVER_MESSAGE_OUTPUT_KEY))
                .isEqualTo(expectedMessageAddressOut);

        List<Deployment> deployments = getClient()
                .resources(Deployment.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .list()
                .getItems();
        assertThat(deployments).hasSize(1);
        assertThat(deployments.get(0).getMetadata().getName()).isEqualTo("test-solver");
    }

    @Test
    void dynamicScaling_configuresKeda() {
        final String solverName = "test-solver";
        final AmqBroker amqBroker = createAmqBroker();
        final int maxReplicas = 5;

        final OptaPlannerSolver solver = new OptaPlannerSolver();
        solver.getMetadata().setName(solverName);
        solver.setSpec(new OptaPlannerSolverSpec());
        solver.getSpec().setSolverImage("solver-project-image");
        solver.getSpec().setAmqBroker(amqBroker);
        solver.getSpec().setScaling(new Scaling(true, maxReplicas));

        getClient().resources(OptaPlannerSolver.class).create(solver);
        await().ignoreException(NullPointerException.class).atMost(1, MINUTES).untilAsserted(() -> {
            OptaPlannerSolver updatedSolver = getClient()
                    .resources(OptaPlannerSolver.class)
                    .inNamespace(solver.getMetadata().getNamespace())
                    .withName(solver.getMetadata().getName())
                    .get();
            assertThat(updatedSolver.getStatus()).isNotNull();
        });

        ScaledObject scaledObject = getClient()
                .resources(ScaledObject.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .withName(solverName)
                .get();

        TriggerAuthentication triggerAuthentication = getClient()
                .resources(TriggerAuthentication.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .withName(solverName)
                .get();

        assertThat(scaledObject.getSpec().getScaleTargetRef().getName()).isEqualTo(solverName);
        assertThat(scaledObject.getSpec().getTriggers()).hasSize(1);
        Trigger trigger = scaledObject.getSpec().getTriggers().get(0);
        assertThat(trigger.getType()).isEqualTo(ScaledObjectDependentResource.ARTEMIS_QUEUE_TRIGGER);
        assertThat(trigger.getMetadata().getQueueName()).isEqualTo(solver.getInputMessageAddressName());
        assertThat(trigger.getAuthenticationRef().getName()).isEqualTo(triggerAuthentication.getMetadata().getName());

        List<SecretTargetRef> secretTargetRefs = triggerAuthentication.getSpec().getSecretTargetRefs();
        assertThat(secretTargetRefs).hasSize(2);
        assertThat(secretTargetRefs).allSatisfy(secretTargetRef -> {
            if (TriggerAuthenticationDependentResource.PARAM_USERNAME.equals(secretTargetRef.getParameter())) {
                assertSecretTargetRefFromSecretKeySelector(secretTargetRef, amqBroker.getUsernameSecretRef());
            } else {
                assertSecretTargetRefFromSecretKeySelector(secretTargetRef, amqBroker.getPasswordSecretRef());
            }
        });
    }

    private KubernetesClient getClient() {
        return getMockServer().getClient().inNamespace(namespace);
    }

    private void assertSecretTargetRefFromSecretKeySelector(SecretTargetRef secretTargetRef,
            SecretKeySelector secretKeySelector) {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(secretTargetRef.getName()).isEqualTo(secretKeySelector.getName());
            softly.assertThat(secretTargetRef.getKey()).isEqualTo(secretKeySelector.getKey());
        });
    }

    private AmqBroker createAmqBroker() {
        AmqBroker amqBroker = new AmqBroker();
        amqBroker.setHost("amq-host");
        amqBroker.setPort(5678);
        amqBroker.setUsernameSecretRef(new SecretKeySelector("amq-username", "my-secret", false));
        amqBroker.setPasswordSecretRef(new SecretKeySelector("amq-password", "my-secret", false));

        return amqBroker;
    }
}
