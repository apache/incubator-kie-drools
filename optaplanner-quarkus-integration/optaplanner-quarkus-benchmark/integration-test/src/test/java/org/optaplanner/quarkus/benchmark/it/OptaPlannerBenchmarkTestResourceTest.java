package org.optaplanner.quarkus.benchmark.it;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;

/**
 * Test various OptaPlanner benchmarking operations running in Quarkus
 */

@QuarkusTest
class OptaPlannerBenchmarkTestResourceTest {

    @Test
    @Timeout(600)
    void benchmark() throws Exception {
        String benchmarkResultDirectory = RestAssured.given()
                .header("Content-Type", "text/plain;charset=UTF-8")
                .when()
                .post("/optaplanner/test/benchmark")
                .body().asString();
        Assertions.assertNotNull(benchmarkResultDirectory);
        Path benchmarkResultDirectoryPath = Path.of(benchmarkResultDirectory);
        Assertions.assertTrue(Files.isDirectory(benchmarkResultDirectoryPath));
        Path benchmarkResultPath = Files.walk(benchmarkResultDirectoryPath, 2)
                .filter(path -> path.endsWith("plannerBenchmarkResult.xml")).findFirst().orElseThrow();
        Assertions.assertTrue(Files.isRegularFile(benchmarkResultPath));
        XmlPath xmlPath = XmlPath.from(benchmarkResultPath.toFile());
        Assertions.assertTrue(xmlPath.getBoolean(
                "plannerBenchmarkResult.solverBenchmarkResult.singleBenchmarkResult.subSingleBenchmarkResult.succeeded"));
    }

}
