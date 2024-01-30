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
package org.kie.dmn.model.v1_5;

import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.UnaryTests;

import java.util.ArrayList;
import java.util.List;

public class TDecisionRule extends TDMNElement implements DecisionRule {

    protected List<UnaryTests> inputEntry;
    protected List<LiteralExpression> outputEntry;
    protected List<RuleAnnotation> annotationEntry;

    @Override
    public List<UnaryTests> getInputEntry() {
        if (inputEntry == null) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    @Override
    public List<LiteralExpression> getOutputEntry() {
        if (outputEntry == null) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    @Override
    public List<RuleAnnotation> getAnnotationEntry() {
        if (annotationEntry == null) {
            annotationEntry = new ArrayList<>();
        }
        return this.annotationEntry;
    }

}
