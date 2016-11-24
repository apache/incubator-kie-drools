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

import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.core.api.event.InternalDMNRuntimeEventManager;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.model.v1_1.Decision;
import org.kie.dmn.feel.runtime.decisiontables.DTInvokerFunction;
import org.kie.dmn.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.FEELEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionNode extends DMNBaseNode implements DMNNode {

    private Decision decision;
    private Map<String, DMNNode> dependencies = new HashMap<>(  );
    private DecisionEvaluator evaluator;
    private DMNType resultType;

    public DecisionNode() {
    }

    public DecisionNode(Decision decision, DMNType resultType ) {
        super( decision );
        this.decision = decision;
        this.resultType = resultType;
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

    public DMNType getResultType() {
        return resultType;
    }

    public static interface DecisionEvaluator {
        Object evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result);
    }

    public static class LiteralExpressionFEELEvaluator implements DecisionEvaluator {
        private CompiledExpression expression;

        public LiteralExpressionFEELEvaluator(CompiledExpression expression) {
            this.expression = expression;
        }

        @Override
        public Object evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
            Object val = FEEL.newInstance().evaluate( expression, result.getContext().getAll() );
            return val;
        }
    }

    public static class DTExpressionEvaluator implements DecisionEvaluator, FEELEventListener {
        private final Decision decision;
        private DTInvokerFunction dt;
        private FEELImpl feel;

        private List<FEELEvent> events = new ArrayList<>(  );

        public DTExpressionEvaluator(Decision decision, DTInvokerFunction dt) {
            this.decision = decision;
            this.dt = dt;
            feel = (FEELImpl) FEEL.newInstance();
            feel.addListener( this );
        }

        @Override
        public Object evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
            try {
                eventManager.fireBeforeEvaluateDecisionTable( dt.getName(), result );
                List<String> paramNames = dt.getParameterNames().get( 0 );
                Object[] params = new Object[ paramNames.size() ];
                EvaluationContextImpl ctx = new EvaluationContextImpl( feel.getEventsManager() );
                for( int i = 0; i < params.length; i++ ) {
                    params[i] = feel.evaluate( paramNames.get( i ), result.getContext().getAll() );
                    ctx.setValue( paramNames.get( i ), params[i] );
                }
                Object dtr = dt.apply( ctx, params );
                processEvents( events, eventManager, result );
                return dtr;
            } finally {
                eventManager.fireAfterEvaluateDecisionTable( dt.getName(), result );
            }
        }

        private void processEvents(List<FEELEvent> events, InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
            for( FEELEvent e : events ) {
                if( e.getSeverity() == FEELEvent.Severity.ERROR ) {
                    result.addMessage( DMNMessage.Severity.ERROR, e.getMessage(), decision.getId(), e );
                }
            }
        }

        @Override
        public void onEvent(FEELEvent event) {
            this.events.add( event );
        }
    }

}
