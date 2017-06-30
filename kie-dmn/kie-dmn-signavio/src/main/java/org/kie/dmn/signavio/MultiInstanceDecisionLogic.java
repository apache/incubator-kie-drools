/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.signavio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl.DMNCompilerExtension;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNDecisionResultImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MultiInstanceDecisionLogic")
public class MultiInstanceDecisionLogic implements DMNCompilerExtension {

    @XStreamAlias("iterationExpression")
    private String iterationExpression;
    
    @XStreamAlias("iteratorShapeId")
    private String iteratorShapeId;
    
    @XStreamAlias("aggregationFunction")
    private String aggregationFunction;
    
    @XStreamAlias("topLevelDecisionId")
    private String topLevelDecisionId;
    
    public String getIterationExpression() {
        return iterationExpression;
    }

    public String getIteratorShapeId() {
        return iteratorShapeId;
    }

    public String getAggregationFunction() {
        return aggregationFunction;
    }

    public String getTopLevelDecisionId() {
        return topLevelDecisionId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MultiInstanceDecisionLogic [iterationExpression=").append(iterationExpression).append(", iteratorShapeId=").append(iteratorShapeId).append(", aggregationFunction=").append(aggregationFunction).append(", topLevelDecisionId=").append(topLevelDecisionId).append("]");
        return builder.toString();
    }

    @Override
    public void extendCompilation(DMNModelImpl model, DecisionNodeImpl di) {
        // set the evaluator accordingly to Signavio logic.
        di.setEvaluator(new MultiInstanceDecisionNodeEvaluator(this, model, di));
        
        // rearreange error messages to avoid the problem of the missing expression but what was logged on LOG is too later
        List<DMNMessage> messages = model.getMessages();
        messages.removeIf( m -> m.getSourceId().equals(di.getSource().getId()) && m.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) );
        
        // Remove the top level decision and its dependencies, from the DMN Model (Decision|BKM|InputData) indexes
        // Remember that as the dependencies will be removed from indexes, are no longer available at evalutation from the DMNExpressionEvaluator
        // hence the MultiInstanceDecisionNodeEvaluator will need to cache anything which is accessed by the DMN Model (Decision|BKM|InputData) indexes 
        DecisionNodeImpl topLevelDecision = (DecisionNodeImpl) model.getDecisionById(topLevelDecisionId);
        recurseNodeToRemoveItAndDepsFromModelIndex(topLevelDecision, model);
    }
    
    private void recurseNodeToRemoveItAndDepsFromModelIndex(DMNNode topLevelDecision, DMNModelImpl model) {
        model.removeDMNNodeFromIndexes(topLevelDecision);
        
        for ( DMNNode dep : ((DMNBaseNode)topLevelDecision).getDependencies().values() ) {
            recurseNodeToRemoveItAndDepsFromModelIndex( dep, model);
        }
    }

    /**
     * Implements the Multi instance Decision node of Signavio as a DMNExpressionEvaluator
     */
    public static class MultiInstanceDecisionNodeEvaluator implements DMNExpressionEvaluator {
        
        private MultiInstanceDecisionLogic mi;
        private DMNModelImpl model;
        private DecisionNodeImpl di;
        private String contextIteratorName;
        private DecisionNodeImpl topLevelDecision;
        
        public MultiInstanceDecisionNodeEvaluator(MultiInstanceDecisionLogic mi, DMNModelImpl model, DecisionNodeImpl di) {
            this.mi = mi;
            this.model = model;
            this.di = di;
            contextIteratorName = model.getInputById( mi.iteratorShapeId ).getName();
            topLevelDecision = (DecisionNodeImpl) model.getDecisionById(mi.topLevelDecisionId);
        }

        @Override
        public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
            DMNResultImpl result = (DMNResultImpl) dmnr;
            DMNContext previousContext = result.getContext();
            DMNContextImpl dmnContext = (DMNContextImpl) previousContext.clone();
            result.setContext( dmnContext );
            
            List<? super Object> invokationResults = new ArrayList<>();
            
            try {
                Object cycleOnRaw = dmnContext.get( mi.iterationExpression );
                Collection<?> cycleOn = null;
                if ( cycleOnRaw instanceof Collection ) {
                    cycleOn = (Collection<?>) cycleOnRaw;
                } else {
                    cycleOn = Arrays.asList(cycleOnRaw);
                }
                for ( Object cycledValue : cycleOn ) {
                    DMNContext nonCycledContext = result.getContext();
                    DMNContextImpl cyclingContext = (DMNContextImpl) nonCycledContext.clone();
                    result.setContext( cyclingContext );
                    
                    cyclingContext.set(contextIteratorName, cycledValue);
                    Object evaluationResult = topLevelDecision.getEvaluator().evaluate(eventManager, result).getResult();
                    invokationResults.add(evaluationResult);
                    
                    result.setContext( nonCycledContext );
                }
            } finally {
                result.setContext( previousContext );
            }
            
            return new EvaluatorResultImpl(invokationResults, ResultType.SUCCESS);
        }
        
    }

}
