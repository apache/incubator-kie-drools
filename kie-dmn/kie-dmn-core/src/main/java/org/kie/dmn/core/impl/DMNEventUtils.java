/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DMNEventUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DMNEventUtils.class);

    public static Map<String, Object> extractBKMParameters(BeforeInvokeBKMEvent event) {
        Map<String, Object> results = new LinkedHashMap<String, Object>();
        BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) event.getBusinessKnowledgeModel();
        List<String> names = ((DMNFunctionDefinitionEvaluator) bkmi.getEvaluator()).getParameterNames().get(0);
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Object value = event.getInvocationParameters().get(i);
            results.put(name, value);
        }
        return results;
    }

    private DMNEventUtils() {
        // Constructing instances is not allowed for this class
    }

}
