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
package org.kie.kogito.jitexecutor.dmn;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNRuntimeImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.jitexecutor.dmn.TestingUtils.getModelFromIoUtils;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DMNEvaluatorTest {

    private static String model;
    private static String invalidModel;

    @BeforeAll
    public static void setup() throws IOException {
        model = getModelFromIoUtils("invalid_models/DMNv1_x/test.dmn");
        invalidModel = getModelFromIoUtils("invalid_models/DMNv1_5/DMN-Invalid.dmn");
    }

    @Test
    void testFromXMLSuccessModel() {
        String modelXML = model;

        DMNEvaluator evaluator = DMNEvaluator.fromXML(modelXML);
        assertNotNull(evaluator);
    }

    @Test
    public void testFromXMLModelWithError() {
        String modelXML = invalidModel;

        assertThrows(IllegalStateException.class, () -> {
            DMNEvaluator.fromXML(modelXML);
        });
    }

    @Test
    void testValidateForErrorsThrowsException() {
        DMNModel dmnModel = mock(DMNModel.class);
        DMNRuntime dmnRuntime = mock(DMNRuntime.class);
        DMNMessage message = mock(DMNMessage.class);

        String errorMessage = "Error compiling FEEL expression 'Person Age >= 18' for name 'Can Drive?' on node 'Can Drive?': syntax error near 'Age'";
        when(message.getText()).thenReturn(errorMessage);
        when(dmnModel.hasErrors()).thenReturn(true);
        when(dmnModel.getMessages(DMNMessage.Severity.ERROR)).thenReturn(Collections.singletonList(message));

        assertThrows(IllegalStateException.class,
                () -> DMNEvaluator.validateForErrors(dmnModel, dmnRuntime), errorMessage);
    }

    @Test
    void testValidateForErrors() {
        DMNModel dmnModel = mock(DMNModel.class);
        DMNRuntime dmnRuntime = mock(DMNRuntimeImpl.class);

        when(dmnModel.hasErrors()).thenReturn(false);
        DMNEvaluator evaluator = DMNEvaluator.validateForErrors(dmnModel, dmnRuntime);

        assertNotNull(evaluator);
    }

}
