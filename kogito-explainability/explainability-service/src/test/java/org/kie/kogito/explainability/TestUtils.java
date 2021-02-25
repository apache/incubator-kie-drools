/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.models.ExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

public class TestUtils {

    private TestUtils() {
        // prevent initialization
    }

    public static final String EXECUTION_ID = "executionId";
    public static final String SERVICE_URL = "localhost:8080";

    public static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("dmn", "name:namespace");

    public static final Value<Boolean> VALUE = new Value<>(true);

    public static final FeatureImportance FEATURE_IMPORTANCE_1 = new FeatureImportance(new Feature("input1", Type.NUMBER, new Value<>(1)), 0.6);
    public static final FeatureImportance FEATURE_IMPORTANCE_2 = new FeatureImportance(new Feature("input2", Type.NUMBER, new Value<>(2)), 0.5);

    public static final List<FeatureImportance> FEATURE_IMPORTANCES = asList(FEATURE_IMPORTANCE_1, FEATURE_IMPORTANCE_2);

    public static final Output OUTPUT = new Output("key", Type.BOOLEAN, VALUE, 1d);
    public static final Saliency SALIENCY = new Saliency(OUTPUT, FEATURE_IMPORTANCES);

    public static final Map<String, Saliency> SALIENCY_MAP = singletonMap("key", SALIENCY);

    public static final Map<String, TypedValue> INPUTS = new HashMap<>();
    static {
        INPUTS.put("input1", new UnitValue("string", new TextNode("value")));
        INPUTS.put("input2", new UnitValue("number", new DoubleNode(10)));
    }

    public static final Map<String, TypedValue> OUTPUTS = singletonMap("output1", new UnitValue("string", new TextNode("output")));

    public static final ExplainabilityRequest REQUEST = new ExplainabilityRequest(EXECUTION_ID, SERVICE_URL, MODEL_IDENTIFIER, INPUTS, OUTPUTS);

}
