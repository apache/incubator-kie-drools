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
package org.drools.ruleunits.dsl.patterns;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.constraints.Constraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.PatternDSL.pattern;

public abstract class SinglePatternDef<A> implements InternalPatternDef {
    protected final RuleDefinition rule;
    protected final Variable<A> variable;
    protected final List<Constraint> constraints = new ArrayList<>();

    protected SinglePatternDef(RuleDefinition rule, Variable<A> variable) {
        this.rule = rule;
        this.variable = variable;
    }

    protected List<Constraint> getConstraints() {
        return constraints;
    }

    public Variable getVariable() {
        return variable;
    }

    public <G> void execute(G globalObject, Block1<G> block) {
        rule.execute(globalObject, block);
    }

    @Override
    public ViewItem toExecModelItem() {
        PatternDSL.PatternDef patternDef = pattern(getVariable());
        for (Constraint constraint : getConstraints()) {
            constraint.addConstraintToPattern(patternDef);
        }
        return patternDef;
    }
}
