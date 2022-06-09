package org.optaplanner.operator.impl.solver;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.operator.impl.solver.model.ConfigMapDependentResource;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;
import org.optaplanner.operator.impl.solver.model.OptaPlannerSolverSpec;
import org.optaplanner.operator.impl.solver.model.Scaling;
import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.javaoperatorsdk.operator.Operator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

@WithKubernetesTestServer
@QuarkusTest
public class OptaPlannerSolverReconcilerTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    Operator operator;

    @BeforeEach
    void startOperator() {
        operator.start();
    }

    @AfterEach
    void stopOperator() {
        operator.stop();
    }

    @Test
    void canReconcile() {

        final OptaPlannerSolver solver = new OptaPlannerSolver();
        final String solverName = "test-solver";
        solver.getMetadata().setName(solverName);
        solver.setSpec(new OptaPlannerSolverSpec());
        solver.getSpec().setSolverImage("solver-project-image");
        solver.getSpec().setKafkaBootstrapServers("kafkaServers");
        solver.getSpec().setKafkaCluster("my-kafka-cluster");
        solver.getSpec().setScaling(new Scaling());
        mockServer.getClient().resources(OptaPlannerSolver.class).create(solver);

        final String expectedMessageAddressIn = solverName + "-" + MessageAddress.INPUT.getName();
        final String expectedMessageAddressOut = solverName + "-" + MessageAddress.OUTPUT.getName();

        await().ignoreException(NullPointerException.class).atMost(1, MINUTES).untilAsserted(() -> {
            OptaPlannerSolver updatedSolver = mockServer.getClient()
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

        ConfigMap configMap = mockServer.getClient()
                .resources(ConfigMap.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .withName(solver.getConfigMapName())
                .get();
        Map<String, String> configMapData = configMap.getData();
        assertThat(configMapData.get(ConfigMapDependentResource.SOLVER_MESSAGE_INPUT_KEY))
                .isEqualTo(expectedMessageAddressIn);
        assertThat(configMapData.get(ConfigMapDependentResource.SOLVER_MESSAGE_OUTPUT_KEY))
                .isEqualTo(expectedMessageAddressOut);

        List<Deployment> deployments = mockServer.getClient()
                .resources(Deployment.class)
                .inNamespace(solver.getMetadata().getNamespace())
                .list()
                .getItems();
        assertThat(deployments).hasSize(1);
        assertThat(deployments.get(0).getMetadata().getName()).isEqualTo("test-solver");
    }
}
