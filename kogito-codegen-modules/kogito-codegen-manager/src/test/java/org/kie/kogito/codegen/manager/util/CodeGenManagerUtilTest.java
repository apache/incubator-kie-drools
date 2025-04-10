/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.manager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.RuleCodegen;
import org.mockito.Mockito;

class CodeGenManagerUtilTest {

    private static final List<String> generatorNames;

    static {
        generatorNames = new ArrayList<>();
        generatorNames.add(RuleCodegen.GENERATOR_NAME);
        generatorNames.add(ProcessCodegen.GENERATOR_NAME);
        generatorNames.add(PredictionCodegen.GENERATOR_NAME);
        generatorNames.add(DecisionCodegen.GENERATOR_NAME);
        generatorNames.add(PersistenceGenerator.GENERATOR_NAME);
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertiesIfNeededWithNullParameters(String generatorName) {
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.ProjectParameters parameters = new CodeGenManagerUtil.ProjectParameters(CodeGenManagerUtil.Framework.QUARKUS,
                null,
                null,
                null,
                null,
                false);
        CodeGenManagerUtil.overwritePropertiesIfNeeded(kogitoBuildContextMocked, parameters);
        if (generatorName.equals(PersistenceGenerator.GENERATOR_NAME)) {
            Mockito.verify(kogitoBuildContextMocked, Mockito.times(1)).setApplicationProperty(expectedWrittenProperty, "false"); // being a boolean property, it default to false
        } else {
            Mockito.verify(kogitoBuildContextMocked, Mockito.never()).setApplicationProperty(Mockito.eq(expectedWrittenProperty), Mockito.any());
        }
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertiesIfNeededWithEmptyParameters(String generatorName) {
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.ProjectParameters parameters = new CodeGenManagerUtil.ProjectParameters(CodeGenManagerUtil.Framework.QUARKUS,
                "",
                "",
                "",
                "",
                false);
        CodeGenManagerUtil.overwritePropertiesIfNeeded(kogitoBuildContextMocked, parameters);
        if (generatorName.equals(PersistenceGenerator.GENERATOR_NAME)) {
            Mockito.verify(kogitoBuildContextMocked, Mockito.times(1)).setApplicationProperty(expectedWrittenProperty, "false"); // being a boolean property, it default to false
        } else {
            Mockito.verify(kogitoBuildContextMocked, Mockito.never()).setApplicationProperty(Mockito.eq(expectedWrittenProperty), Mockito.any());
        }
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertiesIfNeededWithNotNullParameters(String generatorName) {
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.ProjectParameters parameters = new CodeGenManagerUtil.ProjectParameters(CodeGenManagerUtil.Framework.QUARKUS,
                "notnull",
                "notnull",
                "notnull",
                "notnull",
                true);
        CodeGenManagerUtil.overwritePropertiesIfNeeded(kogitoBuildContextMocked, parameters);
        if (generatorName.equals(PersistenceGenerator.GENERATOR_NAME)) {
            Mockito.verify(kogitoBuildContextMocked, Mockito.times(1)).setApplicationProperty(expectedWrittenProperty, "true");
        } else {
            Mockito.verify(kogitoBuildContextMocked, Mockito.times(1)).setApplicationProperty(Mockito.eq(expectedWrittenProperty), Mockito.eq("notnull"));
        }
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithNotNull(String generatorName) {
        String propertyValue = "notnull";
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        Mockito.verify(kogitoBuildContextMocked, Mockito.times(1)).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithEmpty(String generatorName) {
        String propertyValue = "";
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        Mockito.verify(kogitoBuildContextMocked, Mockito.never()).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithNull(String generatorName) {
        String propertyValue = null;
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = Mockito.mock(KogitoBuildContext.class);
        CodeGenManagerUtil.overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        Mockito.verify(kogitoBuildContextMocked, Mockito.never()).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    static Stream<String> getGeneratorNamesStream() {
        return generatorNames.stream();
    }
}
