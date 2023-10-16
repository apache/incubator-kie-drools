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
package org.kie.kogito.explainability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

public class TestUtils {

    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    private TestUtils() {
        // prevent initialization
    }

    public static final String EXECUTION_ID = UUID.randomUUID().toString();
    public static final String COUNTERFACTUAL_ID = UUID.randomUUID().toString();
    public static final String SOLUTION_ID = UUID.randomUUID().toString();
    public static final String SERVICE_URL = "localhost:8080";

    public static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("dmn", "name:namespace");

    public static final Value VALUE = new Value(true);

    public static final FeatureImportance FEATURE_IMPORTANCE_1 = new FeatureImportance(new Feature("input1", Type.NUMBER, new Value(1)), 0.6);
    public static final FeatureImportance FEATURE_IMPORTANCE_2 = new FeatureImportance(new Feature("input2", Type.NUMBER, new Value(2)), 0.5);

    public static final List<FeatureImportance> FEATURE_IMPORTANCES = asList(FEATURE_IMPORTANCE_1, FEATURE_IMPORTANCE_2);

    public static final Output OUTPUT = new Output("key", Type.BOOLEAN, VALUE, 1d);
    public static final Saliency SALIENCY = new Saliency(OUTPUT, FEATURE_IMPORTANCES);

    public static final Map<String, Saliency> SALIENCY_MAP = singletonMap("key", SALIENCY);

    public static final Collection<NamedTypedValue> LIME_INPUTS = List.of(
            new NamedTypedValue("input1",
                    new UnitValue("string", new TextNode("value"))),
            new NamedTypedValue("input2",
                    new UnitValue("number", new DoubleNode(10))));

    public static final Collection<NamedTypedValue> LIME_OUTPUTS = List.of(new NamedTypedValue("output1",
            new UnitValue("string", new TextNode("output"))));

    public static final LIMEExplainabilityRequest LIME_REQUEST = new LIMEExplainabilityRequest(EXECUTION_ID,
            SERVICE_URL,
            MODEL_IDENTIFIER,
            LIME_INPUTS,
            LIME_OUTPUTS);

    public static final Collection<NamedTypedValue> CF_ORIGINAL_INPUTS = List.of(new NamedTypedValue("input1",
            new UnitValue("string", new TextNode("value"))),
            new NamedTypedValue("input2",
                    new UnitValue("number", new DoubleNode(10))));

    public static final Collection<NamedTypedValue> CF_GOALS = List.of(new NamedTypedValue("input1",
            new UnitValue("string", new TextNode("value"))),
            new NamedTypedValue("input2",
                    new UnitValue("number", new DoubleNode(10))));

    public static final Collection<CounterfactualSearchDomain> CF_SEARCH_DOMAINS = new ArrayList<>();

    public static final CounterfactualExplainabilityRequest COUNTERFACTUAL_REQUEST =
            new CounterfactualExplainabilityRequest(EXECUTION_ID,
                    SERVICE_URL,
                    MODEL_IDENTIFIER,
                    COUNTERFACTUAL_ID,
                    CF_ORIGINAL_INPUTS,
                    CF_GOALS,
                    CF_SEARCH_DOMAINS,
                    MAX_RUNNING_TIME_SECONDS);

    public static final CounterfactualResult COUNTERFACTUAL_RESULT = new CounterfactualResult(Collections.emptyList(),
            Collections.emptyList(),
            List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 2.0)))),
            true,
            UUID.fromString(SOLUTION_ID),
            UUID.fromString(EXECUTION_ID),
            0);

}
