/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core.alphasupport;

import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkBuilderContext;
import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation;
import org.kie.dmn.core.compiler.alphanetbased.PropertyEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// All implementations are used only for templating purposes and should never be called
public class AlphaNodeCreationTemplate {

    Logger logger = LoggerFactory.getLogger(AlphaNodeCreationTemplate.class);

    private AlphaNetworkCreation alphaNetworkCreation;

    public AlphaNodeCreationTemplate(AlphaNetworkBuilderContext ctx) {
        alphaNetworkCreation = new AlphaNetworkCreation(ctx);
    }

    // There will be one of this for each column
    // ref: https://github.com/kiegroup/drools/blame/cde68c4b3aee560259387373bea27b607a811c72/kie-dmn/kie-dmn-core/src/main/java/org/kie/dmn/core/compiler/DMNEvaluatorCompiler.java#L710-L713
    boolean testRxCx(PropertyEvaluator x) {
        return UnaryTestRXCX.getInstance().getUnaryTests()
                .stream()
                .anyMatch(t -> {
                    Object value = x.getValue(99999);
                    Boolean result = t.apply(x.getEvaluationContext(), value);
                    if(logger.isTraceEnabled()) {
                        logger.trace("Result for " + "UnaryTestRXCX" + " on values " + x + ": " + result);
                    }
                    return result != null && result;
                });
    }

    Object outputRxCx(org.kie.dmn.feel.lang.EvaluationContext x) {
        return OutputRXCX.getInstance().apply(x);
    }
}
