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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        String errorMessage = "DMN: Error compiling FEEL expression 'Person Age >= 18' for name 'Can Drive?' on node 'Can Drive?': syntax error near 'Age' (DMN id: _563E78C7-EFD1-4109-9F30-B14922EF68DF, Error compiling the referenced FEEL expression) ";
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dmnRuntime.evaluateAll(dmnModel, context);
        });
        assertEquals(errorMessage, exception.getMessage());
        }

    @Test
    void evaluateWrongDecisionWithoutInputDataReferencesByName() {
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
        String errorMessage = "DMN: Error compiling FEEL expression 'Person Age >= 18' for name 'Can Drive?' on node 'Can Drive?': syntax error near 'Age' (DMN id: _563E78C7-EFD1-4109-9F30-B14922EF68DF, Error compiling the referenced FEEL expression) ";
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dmnRuntime.evaluateByName(dmnModel, context, "Can Drive");
        });
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void evaluateRightDecisionWithoutInputDataReferencesByName() {
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

    @Test
    void evaluateWrongDecisionWithoutInputDataReferencesById() {
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
        String errorMessage = "DMN: Error compiling FEEL expression 'Person Age >= 18' for name 'Can Drive?' on node 'Can Drive?': syntax error near 'Age' (DMN id: _563E78C7-EFD1-4109-9F30-B14922EF68DF, Error compiling the referenced FEEL expression) ";
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dmnRuntime.evaluateById(dmnModel, context, "_563E78C7-EFD1-4109-9F30-B14922EF68DF");
        });
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void evaluateRightDecisionWithoutInputDataReferencesById() {
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
        DMNResult dmnResult = dmnRuntime.evaluateById(dmnModel, context, "_7ACCB8BC-A382-4530-B8EE-AD32D187FD8B");
        assertThat(dmnResult).isNotNull();
    }

    @Test
    void evaluateDecisionWithInvalidFeelError() {
        File modelFile = FileUtils.getFile("InvalidFeel.dmn");
        assertThat(modelFile).isNotNull().exists();
        Resource modelResource = ResourceFactory.newFileResource(modelFile);
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_9DF86C49-C80A-4744-9F50-BCE65A89C98C";

        final DMNModel dmnModel = dmnRuntime.getModel(
                nameSpace,
                "DMN_33900B8B-73DD-4D1E-87E9-F6C3FE534B43");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set( "Person Age", 24 );
        String errorMessage = "DMN: Error compiling FEEL expression 'Person Age >?= 18' for name 'Can Drive' on node 'Can Drive': Unknown variable '?' (DMN id: _F477B6E0-C617-4087-9648-DE25A711C5F9, Error compiling the referenced FEEL expression) ";
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dmnRuntime.evaluateByName(dmnModel, context, "Can Drive");
        });
        assertEquals(errorMessage, exception.getMessage());
    }

}