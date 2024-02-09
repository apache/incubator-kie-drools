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
package org.kie.dmn.core.alphasupport;

import java.lang.Override;
import java.util.Optional;

import org.drools.core.common.DefaultFactHandle;
import org.drools.ancompiler.CompiledNetwork;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.alphanetbased.DMNAlphaNetworkEvaluator;
import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkEvaluationContext;
import org.kie.dmn.core.compiler.alphanetbased.Results;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.core.compiler.alphanetbased.PropertyEvaluator;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTable;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

// All implementations are used only for templating purposes and should never be called
public class DMNAlphaNetworkTemplate implements DMNAlphaNetworkEvaluator {

    protected final CompiledNetwork compiledNetwork;
    protected final AlphaNetworkEvaluationContext alphaNetworkEvaluationContext;

    private final HitPolicy hitPolicy = HitPolicy.fromString("HIT_POLICY_NAME");

    protected PropertyEvaluator propertyEvaluator;

    public DMNAlphaNetworkTemplate(CompiledNetwork compiledNetwork,
                                   AlphaNetworkEvaluationContext alphaNetworkEvaluationContext) {
        this.compiledNetwork = compiledNetwork;
        this.alphaNetworkEvaluationContext = alphaNetworkEvaluationContext;
    }

    public PropertyEvaluator getOrCreatePropertyEvaluator(EvaluationContext evaluationContext) {
        if(propertyEvaluator == null) {
            propertyEvaluator = new PropertyEvaluator(evaluationContext, "PROPERTY_NAMES");
        }
        return propertyEvaluator;
    }

    @Override
    public Optional<InvalidInputEvent> validate(EvaluationContext evaluationContext) {
        PropertyEvaluator propertyEvaluator = getOrCreatePropertyEvaluator(evaluationContext);

        // Validation Column
        {
            Optional<InvalidInputEvent> resultValidation0 =
                    ValidatorC0.getInstance().validate(evaluationContext,
                                                       propertyEvaluator.getValue(777));
            if (resultValidation0.isPresent()) {
                return resultValidation0;
            }
        }

        return Optional.empty();
    }


    @Override
    public Object evaluate(EvaluationContext evaluationContext, DecisionTable decisionTable) {

        // Clean previous results
        Results results = alphaNetworkEvaluationContext.getResultCollector();
        results.clearResults();

        // init CompiledNetwork with object needed for results,
        compiledNetwork.init(alphaNetworkEvaluationContext);

        // create lambda constraints and results
        compiledNetwork.initConstraintsResults();

        // Fire rete network
        compiledNetwork.propagateAssertObject(new DefaultFactHandle(getOrCreatePropertyEvaluator(evaluationContext)), null, null);

        // Find result with Hit Policy applied
        Object result = results.applyHitPolicy(evaluationContext, hitPolicy, decisionTable);

        return result;
    }
}
