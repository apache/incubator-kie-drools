package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.bootstrap.model.AppArtifact;
import io.quarkus.test.QuarkusProdModeTest;
import io.restassured.RestAssured;

class OptaPlannerProcessorOverridePropertiesAtRuntimeTest {

    private static final String QUARKUS_VERSION = getRequiredProperty("version.io.quarkus");
    private static final String PROJECT_VERSION = getRequiredProperty("project.version");

    private static String getRequiredProperty(String name) {
        final String v = System.getProperty(name);
        if (v == null || v.isEmpty()) {
            throw new IllegalStateException("The system property (" + name + ") has not been set.");
        }
        return v;
    }

    @RegisterExtension
    static final QuarkusProdModeTest config = new QuarkusProdModeTest()
            .setForcedDependencies(Arrays.asList(
                    // TODO: Remove optaplanner-test when https://github.com/kiegroup/optaplanner/pull/1302 is merged?
                    new AppArtifact("org.optaplanner", "optaplanner-test", PROJECT_VERSION),
                    new AppArtifact("io.quarkus", "quarkus-resteasy", QUARKUS_VERSION)))
            // We want to check if these are overridden at runtime
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .overrideConfigKey("quarkus.optaplanner.solver.move-thread-count", "4")
            .overrideConfigKey("quarkus.optaplanner.solver-manager.parallel-solver-count", "1")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class,
                            TestdataQuarkusConstraintProvider.class,
                            OptaPlannerTestResource.class))
            .setRuntimeProperties(getRuntimeProperties())
            .setRun(true);

    private static Map<String, String> getRuntimeProperties() {
        Map<String, String> out = new HashMap<>();
        out.put("quarkus.optaplanner.solver.termination.best-score-limit", "7");
        out.put("quarkus.optaplanner.solver.move-thread-count", "3");
        out.put("quarkus.optaplanner.solver-manager.parallel-solver-count", "10");
        return out;
    }

    // Can't use injection, so we need a resource to fetch the properties
    @Path("/optaplanner/test")
    public static class OptaPlannerTestResource {
        @Inject
        SolverConfig solverConfig;

        @Inject
        SolverManagerConfig solverManagerConfig;

        @GET
        @Path("/solver-config")
        @Produces(MediaType.TEXT_PLAIN)
        public String getSolverConfig() {
            StringBuilder sb = new StringBuilder();
            sb.append("termination.bestScoreLimit=").append(solverConfig.getTerminationConfig().getBestScoreLimit())
                    .append("\n");
            sb.append("moveThreadCount=").append(solverConfig.getMoveThreadCount()).append("\n");
            return sb.toString();
        }

        @GET
        @Path("/solver-manager-config")
        @Produces(MediaType.TEXT_PLAIN)
        public String getSolverManagerConfig() {
            StringBuilder sb = new StringBuilder();
            sb.append("parallelSolverCount=").append(solverManagerConfig.getParallelSolverCount()).append("\n");
            return sb.toString();
        }
    }

    @Test
    void solverConfigPropertiesShouldBeOverwritten() throws IOException {
        Properties solverConfigProperties = new Properties();
        solverConfigProperties.load(RestAssured.given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .when()
                .get("/optaplanner/test/solver-config")
                .asInputStream());
        assertEquals("7", solverConfigProperties.get("termination.bestScoreLimit"));
        assertEquals("3", solverConfigProperties.get("moveThreadCount"));
    }

    @Test
    void solverManagerConfigPropertiesShouldBeOverwritten() throws IOException {
        Properties solverManagerProperties = new Properties();
        solverManagerProperties.load(RestAssured.given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .when()
                .get("/optaplanner/test/solver-manager-config")
                .asInputStream());
        assertEquals("10", solverManagerProperties.get("parallelSolverCount"));
    }

}
