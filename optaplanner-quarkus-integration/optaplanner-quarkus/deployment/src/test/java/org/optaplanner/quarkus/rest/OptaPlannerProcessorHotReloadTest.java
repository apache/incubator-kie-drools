/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.rest;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.quarkus.constraints.TestdataPlanningConstraintProvider;
import org.optaplanner.quarkus.domain.TestdataPlanningEntity;
import org.optaplanner.quarkus.domain.TestdataPlanningSolution;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class OptaPlannerProcessorHotReloadTest {

    // This fails in IntelliJ with "Undeclared build item class", but not in maven. That's normal in Quarkus for now.
    @RegisterExtension
    static final QuarkusDevModeTest test = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataPlanningEntity.class,
                            TestdataPlanningSolution.class, TestdataPlanningConstraintProvider.class,
                            SolverConfigTestResource.class)
                    .addAsResource("solverConfig.xml"));

    @Test
    public void solverConfigHotReload() {
        String resp = RestAssured.get("/solver-config/seconds-spent-limit").asString();
        Assertions.assertEquals("secondsSpentLimit=2", resp);
        test.modifyResourceFile("solverConfig.xml", s -> s.replace("<secondsSpentLimit>2</secondsSpentLimit>",
                "<secondsSpentLimit>9</secondsSpentLimit>"));
        resp = RestAssured.get("/solver-config/seconds-spent-limit").asString();
        Assertions.assertEquals("secondsSpentLimit=9", resp);
    }

}
