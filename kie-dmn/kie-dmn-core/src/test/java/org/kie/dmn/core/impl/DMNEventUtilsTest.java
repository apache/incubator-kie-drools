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
package org.kie.dmn.core.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.kie.dmn.core.compiler.DMNCompilerImpl;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DMNEventUtilsTest {

    @Test
    public void extractDSOutputDecisionsSuccess() {
        String decisionId = "namespace#d1";
        String decisionName = "DecisionOne";
        Object decisionValue = "ResultValue";

        DMNElementReference dmnElementReference = mock(DMNElementReference.class);
        when(dmnElementReference.getHref()).thenReturn("#d1");

        DecisionService decisionService = mock(DecisionService.class);
        when(decisionService.getOutputDecision()).thenReturn(List.of(dmnElementReference));

        DecisionServiceNode decisionServiceNode = mock(DecisionServiceNode.class);
        when(decisionServiceNode.getDecisionService()).thenReturn(decisionService);

        DecisionNode decisionNode = mock(DecisionNode.class);
        when(decisionNode.getName()).thenReturn(decisionName);

        DMNModel dmnModel = mock(DMNModel.class);
        when(dmnModel.getDecisionById(decisionId)).thenReturn(decisionNode);

        DMNContext dmnContext = mock(DMNContext.class);
        when(dmnContext.get(decisionName)).thenReturn(decisionValue);

        DMNResultImpl dmnResult = mock(DMNResultImpl.class);
        when(dmnResult.getModel()).thenReturn(dmnModel);
        when(dmnResult.getContext()).thenReturn(dmnContext);

        AfterEvaluateDecisionServiceEvent event = mock(AfterEvaluateDecisionServiceEvent.class);
        when(event.getDecisionService()).thenReturn(decisionServiceNode);
        when(event.getResult()).thenReturn(dmnResult);

        try (MockedStatic<DMNCompilerImpl> mockedStatic = Mockito.mockStatic(DMNCompilerImpl.class)) {
            mockedStatic.when(() -> DMNCompilerImpl.getReferenceId(dmnElementReference)).thenReturn(decisionId);
            Map<String, Object> resultMap = DMNEventUtils.extractDSOutputDecisionsValues(event);

            assertThat(resultMap).isNotNull();
            assertThat(resultMap.size()).isEqualTo(1);
            assertThat(resultMap.get(decisionName)).isEqualTo(decisionValue);
        }
    }

    @Test
    public void extractDSOutputDecisionsWithDecisionIdNull() {
        String decisionName = "DecisionOne";
        Object decisionValue = "ResultValue";

        DMNElementReference dmnElementReference = mock(DMNElementReference.class);
        when(dmnElementReference.getHref()).thenReturn("#d1");

        DecisionService decisionService = mock(DecisionService.class);
        when(decisionService.getOutputDecision()).thenReturn(List.of(dmnElementReference));

        DecisionServiceNode decisionServiceNode = mock(DecisionServiceNode.class);
        when(decisionServiceNode.getDecisionService()).thenReturn(decisionService);
        DecisionNode decisionNode = mock(DecisionNode.class);
        when(decisionNode.getName()).thenReturn(decisionName);

        DMNModel dmnModel = mock(DMNModel.class);
        Definitions definitions = mock(Definitions.class);
        Import mockImport = mock(Import.class);

        when(dmnModel.getDefinitions()).thenReturn(definitions);
        when(definitions.getImport()).thenReturn(Collections.singletonList(mockImport));
        when(mockImport.getNamespace()).thenReturn("ns");

        DMNContext dmnContext = mock(DMNContext.class);
        when(dmnContext.get(decisionName)).thenReturn(decisionValue);

        DMNResultImpl dmnResult = mock(DMNResultImpl.class);
        when(dmnResult.getModel()).thenReturn(dmnModel);
        when(dmnResult.getContext()).thenReturn(dmnContext);

        AfterEvaluateDecisionServiceEvent event = mock(AfterEvaluateDecisionServiceEvent.class);
        when(event.getDecisionService()).thenReturn(decisionServiceNode);
        when(event.getResult()).thenReturn(dmnResult);

        try (MockedStatic<DMNCompilerImpl> mockedStatic = Mockito.mockStatic(DMNCompilerImpl.class)) {
            mockedStatic.when(() -> DMNCompilerImpl.getReferenceId(dmnElementReference)).thenReturn(null);
            Assertions.assertThatThrownBy(
                            () -> DMNEventUtils.retrieveDecisionNode(dmnModel, null, "ns"))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    void retrieveDecisionNode() {
        DMNModel dmnModel = mock(DMNModel.class);
        DecisionNode decisionNode = mock(DecisionNode.class);

        when(dmnModel.getDecisionById("decision1")).thenReturn(decisionNode);

        DecisionNode result = DMNEventUtils.retrieveDecisionNode(dmnModel, "decision1", "ns");
        assertThat(decisionNode).isEqualTo(result);
    }

    @Test
    void testRetrieveDecisionNode_namespaceMatch() {
        DMNModel dmnModel = mock(DMNModel.class);
        DecisionNode decisionNode = mock(DecisionNode.class);
        Definitions definitions = mock(Definitions.class);
        Import mockImport = mock(Import.class);

        when(dmnModel.getDecisionById("decision1")).thenReturn(null);
        when(dmnModel.getDefinitions()).thenReturn(definitions);
        when(definitions.getImport()).thenReturn(Collections.singletonList(mockImport));
        when(mockImport.getNamespace()).thenReturn("ns");
        when(dmnModel.getDecisionById("ns#decision1")).thenReturn(decisionNode);

        DecisionNode result = DMNEventUtils.retrieveDecisionNode(dmnModel, "decision1", "ns");
        assertThat(decisionNode).isEqualTo(result);
    }

    @Test
    void testRetrieveDecisionNode_notFound() {
        DMNModel dmnModel = mock(DMNModel.class);
        Definitions definitions = mock(Definitions.class);
        Import mockImport = mock(Import.class);

        when(dmnModel.getDecisionById("decision1")).thenReturn(null);
        when(dmnModel.getDefinitions()).thenReturn(definitions);
        when(definitions.getImport()).thenReturn(Collections.singletonList(mockImport));
        when(mockImport.getNamespace()).thenReturn("ns");
        when(dmnModel.getDecisionById("ns#decision1")).thenReturn(null);

        Assertions.assertThatThrownBy(
                        () -> DMNEventUtils.retrieveDecisionNode(dmnModel, "decision1", "ns"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
