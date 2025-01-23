/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.maven.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.RuleCodegen;

import static org.kie.kogito.maven.plugin.AbstractKieMojo.overwritePropertyIfNeeded;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AbstractKieMojoTest {

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
    void overwritePropertiesIfNeededWithNull(String generatorName) {
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        AbstractKieMojo abstractKieMojo = new AbstractKieMojo() {
            @Override
            public void execute() throws MojoExecutionException, MojoFailureException {
            }
        };
        KogitoBuildContext kogitoBuildContextMocked = mock(KogitoBuildContext.class);
        abstractKieMojo.overwritePropertiesIfNeeded(kogitoBuildContextMocked);
        if (generatorName.equals(PersistenceGenerator.GENERATOR_NAME)) {
            verify(kogitoBuildContextMocked, times(1)).setApplicationProperty(expectedWrittenProperty, "false"); // being a boolean property, it default to false
        } else {
            verify(kogitoBuildContextMocked, never()).setApplicationProperty(eq(expectedWrittenProperty), any());
        }
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithNotNull(String generatorName) {
        String propertyValue = "notnull";
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = mock(KogitoBuildContext.class);
        overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        verify(kogitoBuildContextMocked, times(1)).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithEmpty(String generatorName) {
        String propertyValue = "";
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = mock(KogitoBuildContext.class);
        overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        verify(kogitoBuildContextMocked, never()).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    @ParameterizedTest
    @MethodSource("getGeneratorNamesStream")
    void overwritePropertyIfNeededWithNull(String generatorName) {
        String propertyValue = null;
        String expectedWrittenProperty = Generator.CONFIG_PREFIX + generatorName;
        KogitoBuildContext kogitoBuildContextMocked = mock(KogitoBuildContext.class);
        overwritePropertyIfNeeded(kogitoBuildContextMocked, generatorName, propertyValue);
        verify(kogitoBuildContextMocked, never()).setApplicationProperty(expectedWrittenProperty, propertyValue);
    }

    static Stream<String> getGeneratorNamesStream() {
        return generatorNames.stream();
    }

}