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

package org.kie.dmn.core.impl;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntime;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.internal.io.ResourceTypePackage;

import java.util.*;
import java.util.List;

public class DMNRuntimeImpl
        implements DMNRuntime {

    private KieRuntime                         runtime;
    private DMNRuntimeEventManagerImpl         eventManager;

    public DMNRuntimeImpl(KieRuntime runtime) {
        this.runtime = runtime;
        this.eventManager = new DMNRuntimeEventManagerImpl();
    }

    @Override
    public List<DMNModel> getModels() {
        List<DMNModel> models = new ArrayList<>(  );
        runtime.getKieBase().getKiePackages().forEach( kpkg -> {
            DMNPackage dmnPkg = (DMNPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get( ResourceType.DMN );
            if( dmnPkg != null ) {
                dmnPkg.getAllModels().values().forEach( model -> models.add( model ) );
            }
        } );
        return models;
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) runtime.getKieBase().getKiePackage( namespace );
        Map<ResourceType, ResourceTypePackage> map = kpkg.getResourceTypePackages();
        DMNPackage dmnpkg = (DMNPackage) map.get( ResourceType.DMN );
        return dmnpkg != null ? dmnpkg.getModel( modelName ) : null;
    }

    @Override
    public DMNResult evaluateAll(DMNModel model, DMNContext context) {
        DMNResultImpl result = createResult( model, context );
        for( DecisionNode decision : model.getDecisions() ) {
            evaluateDecision( context, result, decision );
        }
        return result;
    }

    @Override
    public DMNResult evaluateDecisionByName(DMNModel model, String decisionName, DMNContext context) {
        DMNResultImpl result = createResult( model, context );
        DecisionNode decision = model.getDecisionByName( decisionName );
        if( decision != null ) {
            evaluateDecision( context, result, decision );
        } else {
            result.addMessage( DMNMessage.Severity.ERROR, "Decision not found for name '"+decisionName+"'", null );
        }
        return result;
    }

    @Override
    public DMNResult evaluateDecisionById(DMNModel model, String decisionId, DMNContext context) {
        DMNResultImpl result = createResult( model, context );
        DecisionNode decision = model.getDecisionById( decisionId );
        if( decision != null ) {
            evaluateDecision( context, result, decision );
        } else {
            result.addMessage( DMNMessage.Severity.ERROR, "Decision not found for id '"+decisionId+"'", null );
        }
        return result;
    }

    @Override
    public void addListener(DMNRuntimeEventListener listener) {
        this.eventManager.addListener( listener );
    }

    @Override
    public void removeListener(DMNRuntimeEventListener listener) {
        this.eventManager.removeListener( listener );
    }

    @Override
    public Set<DMNRuntimeEventListener> getListeners() {
        return this.eventManager.getListeners();
    }

    private DMNResultImpl createResult(DMNModel model, DMNContext context) {
        DMNResultImpl result = new DMNResultImpl();
        result.setContext( context.clone() );

        for( DecisionNode decision : model.getDecisions() ) {
            result.setDecisionResult( decision.getId(), new DMNDecisionResultImpl( decision.getId(), decision.getName() ) );
        }
        return result;
    }

    private void evaluateAllBKM(DMNModel model, DMNContext context, DMNResultImpl result) {
        for( BusinessKnowledgeModelNode bkm : model.getBusinessKnowledgeModels() ) {
            evaluateBKM( context, result, bkm );
        }
    }

    private void evaluateBKM(DMNContext context, DMNResultImpl result, BusinessKnowledgeModelNode b) {
        BusinessKnowledgeModelNodeImpl bkm = (BusinessKnowledgeModelNodeImpl) b;
        if( result.getContext().isDefined( bkm.getName() ) ) {
            // already resolved
            // TODO: do we need to check if the defined variable is a function as it should?
            return;
        }
        // TODO: do we need to check/resolve dependencies?
        if( bkm.getEvaluator() == null ) {
            DMNMessage msg = result.addMessage( DMNMessage.Severity.WARN,
                                                "Missing expression for Business Knowledge Model node '"+getIdentifier( bkm )+"'. Skipping evaluation.",
                                                bkm.getSource() );
            return;
        }
        try {
            eventManager.fireBeforeEvaluateBKM( bkm, result );
            for( DMNNode dep : bkm.getDependencies().values() ) {
                if( ! result.getContext().isDefined( dep.getName() ) ) {
                    if( dep instanceof BusinessKnowledgeModelNode ) {
                        evaluateBKM( context, result, (BusinessKnowledgeModelNode) dep );
                    } else {
                        String message = "Missing dependency for Business Knowledge Model node '" + getIdentifier( bkm ) + "': dependency='" + getIdentifier( dep ) + "'";
                        result.addMessage( DMNMessage.Severity.ERROR, message, bkm.getSource() );
                        return;
                    }
                }
            }

            EvaluatorResult er = bkm.getEvaluator().evaluate( eventManager, result );
            if( er.getResultType() == EvaluatorResult.ResultType.SUCCESS ) {
                result.getContext().set( bkm.getBusinessKnowledModel().getVariable().getName(), er.getResult() );
            }
        } catch( Throwable t ) {
            result.addMessage( DMNMessage.Severity.ERROR, "Error evaluating Business Knowledge Model node '"+getIdentifier( bkm )+ "': "+t.getMessage(), bkm.getSource(), t );
        } finally {
            eventManager.fireAfterEvaluateBKM( bkm, result );
        }
    }

    private boolean evaluateDecision(DMNContext context, DMNResultImpl result, DecisionNode d) {
        DecisionNodeImpl decision = (DecisionNodeImpl) d;
        if( result.getContext().isDefined( decision.getName() ) ) {
            // already resolved
            return true;
        } else {
            // check if the decision was already evaluated before and returned error
            DMNDecisionResult dr = result.getDecisionResultById( decision.getId() );
            if( dr.getEvaluationStatus() == DMNDecisionResult.DecisionEvaluationStatus.FAILED ||
                dr.getEvaluationStatus() == DMNDecisionResult.DecisionEvaluationStatus.SKIPPED ) {
                return false;
            }
        }
        try {
            eventManager.fireBeforeEvaluateDecision( decision, result );
            boolean missingInput = false;
            DMNDecisionResultImpl dr = (DMNDecisionResultImpl) result.getDecisionResultById( decision.getId() );
            for( DMNNode dep : decision.getDependencies().values() ) {
                if( ! result.getContext().isDefined( dep.getName() ) ) {
                    if( dep instanceof DecisionNode ) {
                        if( ! evaluateDecision( context, result, (DecisionNode) dep ) ) {
                            missingInput = true;
                            String message = "Unable to evaluate decision '" + getIdentifier( decision ) + "' as it depends on decision '" + getIdentifier( dep ) + "'";
                            reportFailure( result, decision, dr, null, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                        }
                    } else if( dep instanceof BusinessKnowledgeModelNode ) {
                        evaluateBKM( context, result, (BusinessKnowledgeModelNode) dep );
                    } else {
                        missingInput = true;
                        String message = "Missing dependency for decision '" + getIdentifier( decision ) + "': dependency='" + getIdentifier( dep ) + "'";
                        reportFailure( result, decision, dr, null, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                    }
                }
            }
            if( missingInput ) {
                return false;
            }
            if( decision.getEvaluator() == null ) {
                DMNMessage msg = result.addMessage( DMNMessage.Severity.WARN,
                                                    "Missing expression for decision '"+decision.getName()+"'. Skipping evaluation.",
                                                    decision.getSource() );
                dr.getMessages().add( msg );
                dr.setEvaluationStatus( DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                return false;
            }
            try {
                EvaluatorResult er = decision.getEvaluator().evaluate( eventManager, result );
                if( er.getResultType() == EvaluatorResult.ResultType.SUCCESS ) {
                    Object value = er.getResult();
                    if( ! decision.getResultType().isCollection() && value instanceof Collection &&
                        ((Collection)value).size()==1 ) {
                        // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
                        // and vice-versa
                        value = ((Collection)value).toArray()[0];
                    }
                    result.getContext().set( decision.getDecision().getVariable().getName(), value );
                    dr.setResult( value );
                    dr.setEvaluationStatus( DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED );
                } else {
                    dr.setEvaluationStatus( DMNDecisionResult.DecisionEvaluationStatus.FAILED );
                }
            } catch( Throwable t ) {
                String message = "Error evaluating decision '" + decision.getName() + "': " + t.getMessage();
                reportFailure( result, decision, dr, t, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED );
            }
            return true;
        } finally {
            eventManager.fireAfterEvaluateDecision( decision, result );
        }
    }

    private String getIdentifier(DMNNode node) {
        return node.getName() != null ? node.getName() : node.getId();
    }

    private void reportFailure(DMNResultImpl result, DecisionNode decision, DMNDecisionResultImpl dr, Throwable t, String message, DMNDecisionResult.DecisionEvaluationStatus status) {
        result.addMessage( DMNMessage.Severity.ERROR, message, decision.getDecision(), t );
        DMNMessage msg = result.addMessage( DMNMessage.Severity.ERROR,
                                            message,
                                            decision.getDecision(),
                                            t );
        dr.getMessages().add( msg );
        dr.setEvaluationStatus( status );
    }

    @Override
    public DMNContext newContext() {
        return DMNFactory.newContext();
    }

}
