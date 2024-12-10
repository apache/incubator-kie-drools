/**
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
package org.kie.dmn.core.internal.utils;

import java.io.File;
import java.util.Collections;

import org.drools.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DMNRuntimeBuilderTest {

    private DMNRuntimeBuilder dmnRuntimeBuilder;

    @BeforeEach
    void setup() {
        dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        assertThat(dmnRuntimeBuilder).isNotNull();
    }

    @Test
    void buildFromConfiguration() {
        final DMNRuntimeImpl retrieved = (DMNRuntimeImpl) dmnRuntimeBuilder
                .buildConfiguration()
                .fromResources(Collections.emptyList()).getOrElseThrow(RuntimeException::new);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void fromDefaultsMultipleDecisionWithoutInputDataReference() {
        File modelFile = FileUtils.getFile("Invalid_decisions_model.dmn");
        assertThat(modelFile).isNotNull().exists();
        Resource modelResource = ResourceFactory.newFileResource(modelFile);
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_BDC29BCF-B5DC-4AD7-8A5F-43DC08780F97";

        final DMNModel dmnModel = dmnRuntime.getModel(
                nameSpace,
                "DMN_1A4BD262-7672-4887-9F25-986EE5277D16");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set( "Person Age", 24 );
        assertThatThrownBy(() -> dmnRuntime.evaluateAll(dmnModel, context))
                .isInstanceOf(IllegalStateException.class);
        }

    @Test
    void multipleDecisionWithoutInputDataReferencesEvaluateWrongDecision() {
        File modelFile = FileUtils.getFile("Invalid_decisions_model.dmn");
        assertThat(modelFile).isNotNull().exists();
        Resource modelResource = ResourceFactory.newFileResource(modelFile);
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_BDC29BCF-B5DC-4AD7-8A5F-43DC08780F97";

        final DMNModel dmnModel = dmnRuntime.getModel(
                nameSpace,
                "DMN_1A4BD262-7672-4887-9F25-986EE5277D16");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set( "Person Age", 24 );
        assertThatThrownBy(() -> dmnRuntime.evaluateByName(dmnModel, context, "Can Drive?"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void multipleDecisionWithoutInputDataReferencesValuateCorrectDecision() {
        File modelFile = FileUtils.getFile("Invalid_decisions_model.dmn");
        assertThat(modelFile).isNotNull().exists();
        Resource modelResource = ResourceFactory.newFileResource(modelFile);
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_BDC29BCF-B5DC-4AD7-8A5F-43DC08780F97";

        final DMNModel dmnModel = dmnRuntime.getModel(
                nameSpace,
                "DMN_1A4BD262-7672-4887-9F25-986EE5277D16");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set( "Person Age", 24 );
        DMNResult dmnResult = dmnRuntime.evaluateByName(dmnModel, context, "Can Vote?");
        assertThat(dmnResult).isNotNull();
    }
}