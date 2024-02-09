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
package org.kie.dmn.core.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DMNEventUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DMNEventUtils.class);

    public static Map<String, Object> extractBKMParameters(BeforeInvokeBKMEvent event) {
        Map<String, Object> results = new LinkedHashMap<>();
        BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) event.getBusinessKnowledgeModel();
        List<String> names = ((DMNFunctionDefinitionEvaluator) bkmi.getEvaluator()).getParameterNames().get(0);
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Object value = event.getInvocationParameters().get(i);
            results.put(name, value);
        }
        return results;
    }

    public static Map<String, Object> extractDSParameters(BeforeEvaluateDecisionServiceEvent event) {
        Map<String, Object> results = new LinkedHashMap<>();
        DecisionServiceNodeImpl dsi = (DecisionServiceNodeImpl) event.getDecisionService();
        Map<String, DMNNode> params = dsi.getInputParameters();
        for (Entry<String, DMNNode> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = event.getResult().getContext().get(name);
            results.put(name, value);
        }
        return results;
    }

    public static Map<String, Object> extractDSOutputDecisionsValues(AfterEvaluateDecisionServiceEvent event) {
        Map<String, Object> results = new LinkedHashMap<>();
        List<String> decisionIDs = event.getDecisionService().getDecisionService().getOutputDecision().stream().map(er -> DMNCompilerImpl.getId(er)).collect(Collectors.toList());
        for (String id : decisionIDs) {
            String decisionName = ((DMNResultImpl) event.getResult()).getModel().getDecisionById(id).getName();
            Object decisionResult = event.getResult().getContext().get(decisionName);
            results.put(decisionName, decisionResult);
        }
        return results;
    }

    private DMNEventUtils() {
        // Constructing instances is not allowed for this class
    }

}
