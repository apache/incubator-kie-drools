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
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.ast.DMNNode;
import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.internal.io.ResourceTypePackage;

import java.util.Map;

public class DMNRuntimeImpl
        implements DMNRuntime {

    private KieRuntime runtime;

    public DMNRuntimeImpl(KieRuntime runtime) {
        this.runtime = runtime;
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
        DMNResultImpl result = createResult( context );
        for( DecisionNode decision : model.getDecisions() ) {
            if ( ! evaluateDecision( context, result, decision ) )
                // have to replace this by proper error handling
                return null;
        }
        return result;
    }

    @Override
    public DMNResult evaluateDecisionByName(DMNModel model, String decisionName, DMNContext context) {
        DMNResultImpl result = createResult( context );
        DecisionNode decision = model.getDecisionByName( decisionName );
        if( decision != null ) {
            evaluateDecision( context, result, decision );
        } else {
            result.addMessage( DMNMessage.Severity.ERROR, "Decision not found for name '"+decisionName+"'" );
        }
        return result;
    }

    @Override
    public DMNResult evaluateDecisionById(DMNModel model, String decisionId, DMNContext context) {
        DMNResultImpl result = createResult( context );
        DecisionNode decision = model.getDecisionById( decisionId );
        if( decision != null ) {
            evaluateDecision( context, result, decision );
        } else {
            result.addMessage( DMNMessage.Severity.ERROR, "Decision not found for id '"+decisionId+"'" );
        }
        return result;
    }

    private DMNResultImpl createResult(DMNContext context) {
        DMNResultImpl result = new DMNResultImpl();
        result.setContext( context.clone() );
        return result;
    }

    private boolean evaluateDecision(DMNContext context, DMNResultImpl result, DecisionNode decision) {
        boolean missingInput = false;
        for( DMNNode dep : decision.getDependencies().values() ) {
            if( ! context.isDefined( dep.getName() ) ) {
                if( dep instanceof DecisionNode ) {
                    evaluateDecision( context, result, (DecisionNode) dep );
                } else {
                    missingInput = true;
                    result.addMessage( DMNMessage.Severity.ERROR, "Missing input for decision '"+decision.getName()+"': input name='" + dep.getName() + "' input id='" + dep.getId() + "'" );
                }
            }
        }
        if( missingInput ) {
            return false;
        }
        Object val = decision.getEvaluator().evaluate( result );
        result.getContext().set( decision.getDecision().getVariable().getName(), val );
        result.setDecisionResult( decision.getName(), val );
        return true;
    }


}
