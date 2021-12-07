/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class OptaPlannerBenchmarkTestResourceTest {

    @Test
    @Timeout(600)
    public void benchmark() throws Exception {
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
