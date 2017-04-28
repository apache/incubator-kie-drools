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
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.internal.io.ResourceTypePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;

public class DMNRuntimeImpl
        implements DMNRuntime {
    private static final Logger logger = LoggerFactory.getLogger( DMNRuntimeImpl.class );

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
        if( kpkg == null ) {
            return null;
        }
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
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   null,
                                   result,
                                   null,
                                   null,
                                   Msg.DECISION_NOT_FOUND_FOR_NAME,
                                   decisionName );
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
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   null,
                                   result,
                                   null,
                                   null,
                                   Msg.DECISION_NOT_FOUND_FOR_ID,
                                   decisionId );
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
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.WARN,
                                   bkm.getSource(),
                                   result,
                                   null,
                                   null,
                                   Msg.MISSING_EXPRESSION_FOR_BKM,
                                   getIdentifier( bkm ) );
            return;
        }
        try {
            eventManager.fireBeforeEvaluateBKM( bkm, result );
            for( DMNNode dep : bkm.getDependencies().values() ) {
                if ( !checkDependencyValueIsValid(b, dep, result.getContext()) ) {
                    DMNMessage message = MsgUtil.reportMessage( logger,
                                                                DMNMessage.Severity.ERROR,
                                                                ((DMNBaseNode) dep).getSource(),
                                                                result,
                                                                null,
                                                                null,
                                                                Msg.ERROR_EVAL_NODE_DEP_WRONG_TYPE,
                                                                getIdentifier( bkm ),
                                                                getIdentifier( dep ),
                                                                result.getContext().get( dep.getName() )
                                                                );
                    return;
                }
                if( ! result.getContext().isDefined( dep.getName() ) ) {
                    if( dep instanceof BusinessKnowledgeModelNode ) {
                        evaluateBKM( context, result, (BusinessKnowledgeModelNode) dep );
                    } else {
                        MsgUtil.reportMessage( logger,
                                               DMNMessage.Severity.ERROR,
                                               bkm.getSource(),
                                               result,
                                               null,
                                               null,
                                               Msg.REQ_DEP_NOT_FOUND_FOR_NODE,
                                               getIdentifier( dep ),
                                               getIdentifier( bkm )
                        );
                        return;
                    }
                }
            }

            EvaluatorResult er = bkm.getEvaluator().evaluate( eventManager, result );
            if( er.getResultType() == EvaluatorResult.ResultType.SUCCESS ) {
                FEELFunction resultFn = (FEELFunction) er.getResult();
                // TODO check of the return type will need calculation/inference of function return type.
                result.getContext().set( bkm.getBusinessKnowledModel().getVariable().getName(), resultFn );
            }
        } catch( Throwable t ) {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   bkm.getSource(),
                                   result,
                                   t,
                                   null,
                                   Msg.ERROR_EVAL_BKM_NODE,
                                   getIdentifier( bkm ),
                                   t.getMessage() );
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
                if ( !checkDependencyValueIsValid(d, dep, result.getContext()) ) {
                    missingInput = true;
                    DMNMessage message = MsgUtil.reportMessage( logger,
                            DMNMessage.Severity.ERROR,
                            ((DMNBaseNode) dep).getSource(),
                            result,
                            null,
                            null,
                            Msg.ERROR_EVAL_NODE_DEP_WRONG_TYPE,
                            getIdentifier( decision ),
                            getIdentifier( dep ),
                            result.getContext().get( dep.getName() )
                            );
                    reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                }
                if( ! result.getContext().isDefined( dep.getName() ) ) {
                    if( dep instanceof DecisionNode ) {
                        if( ! evaluateDecision( context, result, (DecisionNode) dep ) ) {
                            missingInput = true;
                            DMNMessage message = MsgUtil.reportMessage( logger,
                                                                        DMNMessage.Severity.ERROR,
                                                                        decision.getSource(),
                                                                        result,
                                                                        null,
                                                                        null,
                                                                        Msg.UNABLE_TO_EVALUATE_DECISION_REQ_DEP,
                                                                        getIdentifier( decision ),
                                                                        getIdentifier( dep ) );
                            reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                        }
                    } else if( dep instanceof BusinessKnowledgeModelNode ) {
                        evaluateBKM( context, result, (BusinessKnowledgeModelNode) dep );
                    } else {
                        missingInput = true;
                        DMNMessage message = MsgUtil.reportMessage( logger,
                                                                    DMNMessage.Severity.ERROR,
                                                                    decision.getSource(),
                                                                    result,
                                                                    null,
                                                                    null,
                                                                    Msg.REQ_DEP_NOT_FOUND_FOR_NODE,
                                                                    getIdentifier( dep ),
                                                                    getIdentifier( decision )
                                                                    );
                        reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
                    }
                }
            }
            if( missingInput ) {
                return false;
            }
            if( decision.getEvaluator() == null ) {
                DMNMessage message = MsgUtil.reportMessage( logger,
                                                            DMNMessage.Severity.WARN,
                                                            decision.getSource(),
                                                            result,
                                                            null,
                                                            null,
                                                            Msg.MISSING_EXPRESSION_FOR_DECISION,
                                                            getIdentifier( decision ) );

                reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED );
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
                    
                    if ( !d.getResultType().isAssignableValue(value) ) {
                        DMNMessage message = MsgUtil.reportMessage( logger,
                                DMNMessage.Severity.ERROR,
                                decision.getSource(),
                                result,
                                null,
                                null,
                                Msg.ERROR_EVAL_NODE_RESULT_WRONG_TYPE,
                                getIdentifier( decision ),
                                decision.getResultType(),
                                value);
                        reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED );
                        return false;
                    }
                    
                    result.getContext().set( decision.getDecision().getVariable().getName(), value );
                    dr.setResult( value );
                    dr.setEvaluationStatus( DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED );
                } else {
                    dr.setEvaluationStatus( DMNDecisionResult.DecisionEvaluationStatus.FAILED );
                }
            } catch( Throwable t ) {
                DMNMessage message = MsgUtil.reportMessage( logger,
                                                            DMNMessage.Severity.ERROR,
                                                            decision.getSource(),
                                                            result,
                                                            t,
                                                            null,
                                                            Msg.ERROR_EVAL_DECISION_NODE,
                                                            getIdentifier( decision ),
                                                            t.getMessage() );

                reportFailure( dr, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED );
            }
            return true;
        } finally {
            eventManager.fireAfterEvaluateDecision( decision, result );
        }
    }

    private boolean checkDependencyValueIsValid(DMNNode currentNode, DMNNode dep, DMNContext context) {
        if (dep instanceof InputDataNode) {
            InputDataNode inputDataNode = (InputDataNode) dep;
            BaseDMNTypeImpl dmnType = (BaseDMNTypeImpl) inputDataNode.getType();
            if ( dmnType.getAllowedValues() != null && !dmnType.getAllowedValues().isEmpty() ) {
                List<UnaryTest> allowedValues = dmnType.getAllowedValues();
                return DMNFEELHelper.valueMatchesInUnaryTests(allowedValues, context.get( dep.getName() ), context);
            }
        }
        return true;
    }

    private String getIdentifier(DMNNode node) {
        return node.getName() != null ? node.getName() : node.getId();
    }

    private void reportFailure(DMNDecisionResultImpl dr, DMNMessage message, DMNDecisionResult.DecisionEvaluationStatus status) {
        dr.getMessages().add( message );
        dr.setEvaluationStatus( status );
    }

    @Override
    public DMNContext newContext() {
        return DMNFactory.newContext();
    }

}
