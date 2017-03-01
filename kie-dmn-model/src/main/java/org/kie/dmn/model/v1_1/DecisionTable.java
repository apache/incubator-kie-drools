/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

public class DecisionTable extends Expression {

    private List<InputClause> input;
    private List<OutputClause> output;
    private List<DecisionRule> rule;
    private HitPolicy hitPolicy;
    private BuiltinAggregator aggregation;
    private DecisionTableOrientation preferredOrientation;
    private String outputLabel;

    public List<InputClause> getInput() {
        if ( input == null ) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    public List<OutputClause> getOutput() {
        if ( output == null ) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    public List<DecisionRule> getRule() {
        if ( rule == null ) {
            rule = new ArrayList<>();
        }
        return this.rule;
    }

    public HitPolicy getHitPolicy() {
        if ( hitPolicy == null ) {
            return HitPolicy.UNIQUE;
        } else {
            return hitPolicy;
        }
    }

    public void setHitPolicy( final HitPolicy value ) {
        this.hitPolicy = value;
    }

    public BuiltinAggregator getAggregation() {
        return aggregation;
    }

    public void setAggregation( final BuiltinAggregator value ) {
        this.aggregation = value;
    }

    public DecisionTableOrientation getPreferredOrientation() {
        if ( preferredOrientation == null ) {
            return DecisionTableOrientation.RULE_AS_ROW;
        } else {
            return preferredOrientation;
        }
    }

    public void setPreferredOrientation( final DecisionTableOrientation value ) {
        this.preferredOrientation = value;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel( final String value ) {
        this.outputLabel = value;
    }

}
