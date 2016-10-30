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

package org.kie.dmn.core.ast;

import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.model.v1_1.Decision;
import org.kie.dmn.feel.model.v1_1.LiteralExpression;
import org.kie.dmn.feel.runtime.decisiontables.ConcreteDTFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionNode extends DMNBaseNode implements DMNNode {

    private Decision decision;
    private Map<String, DMNNode> dependencies = new HashMap<>(  );
    private DecisionEvaluator evaluator;

    public DecisionNode() {
    }

    public DecisionNode(Decision decision) {
        super( decision );
        this.decision = decision;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Map<String, DMNNode> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, DMNNode> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency( String name, DMNNode dependency ) {
        this.dependencies.put( name, dependency );
    }

    public DecisionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(DecisionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public static interface DecisionEvaluator {
        public Object evaluate(DMNResultImpl result);
    }

    public static class LiteralExpressionFEELEvaluator implements DecisionEvaluator {
        private CompiledExpression expression;

        public LiteralExpressionFEELEvaluator(CompiledExpression expression) {
            this.expression = expression;
        }

        @Override
        public Object evaluate(DMNResultImpl result) {
            Object val = FEEL.newInstance().evaluate( expression, result.getContext().getAll() );
            return val;
        }
    }

    public static class DTExpressionEvaluator implements DecisionEvaluator {
        private ConcreteDTFunction dt;

        public DTExpressionEvaluator(ConcreteDTFunction dt) {
            this.dt = dt;
        }

        @Override
        public Object evaluate(DMNResultImpl result) {
            List<String> paramNames = dt.getParameterNames().get( 0 );
            Object[] params = new Object[ paramNames.size() ];
            for( int i = 0; i < params.length; i++ ) {
                params[i] = result.getContext().get( paramNames.get( i ) );
            }
            Object dtr = dt.apply( new EvaluationContextImpl(), params );
            return dtr;
        }
    }


}
