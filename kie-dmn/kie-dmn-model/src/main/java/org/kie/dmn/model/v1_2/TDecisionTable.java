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
package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.BuiltinAggregator;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.DecisionTableOrientation;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.RuleAnnotationClause;


public class TDecisionTable extends TExpression implements DecisionTable {

    protected List<InputClause> input;
    protected List<OutputClause> output;
    protected List<RuleAnnotationClause> annotation;
    protected List<DecisionRule> rule;
    protected HitPolicy hitPolicy;
    protected BuiltinAggregator aggregation;
    protected DecisionTableOrientation preferredOrientation;
    protected String outputLabel;

    @Override
    public List<InputClause> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    @Override
    public List<OutputClause> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    @Override
    public List<RuleAnnotationClause> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        return this.annotation;
    }

    @Override
    public List<DecisionRule> getRule() {
        if (rule == null) {
            rule = new ArrayList<>();
        }
        return this.rule;
    }

    @Override
    public HitPolicy getHitPolicy() {
        if (hitPolicy == null) {
            return HitPolicy.UNIQUE;
        } else {
            return hitPolicy;
        }
    }

    @Override
    public void setHitPolicy(HitPolicy value) {
        this.hitPolicy = value;
    }

    @Override
    public BuiltinAggregator getAggregation() {
        return aggregation;
    }

    @Override
    public void setAggregation(BuiltinAggregator value) {
        this.aggregation = value;
    }

    @Override
    public DecisionTableOrientation getPreferredOrientation() {
        if (preferredOrientation == null) {
            return DecisionTableOrientation.RULE_AS_ROW;
        } else {
            return preferredOrientation;
        }
    }

    @Override
    public void setPreferredOrientation(DecisionTableOrientation value) {
        this.preferredOrientation = value;
    }

    @Override
    public String getOutputLabel() {
        return outputLabel;
    }

    @Override
    public void setOutputLabel(String value) {
        this.outputLabel = value;
    }

}
