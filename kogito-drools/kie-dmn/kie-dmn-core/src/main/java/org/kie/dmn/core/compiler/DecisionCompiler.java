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

package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.v1_1.DRGElement;
import org.kie.dmn.model.v1_1.Decision;

public class DecisionCompiler implements DRGElementCompiler {
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof Decision;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        Decision decision = (Decision) de;
        DecisionNodeImpl dn = new DecisionNodeImpl( decision );
        DMNType type = null;
        if ( decision.getVariable() == null ) {
            DMNCompilerHelper.reportMissingVariable( model, de, decision, Msg.MISSING_VARIABLE_FOR_DECISION );
            return;
        }
        DMNCompilerHelper.checkVariableName( model, decision, decision.getName() );
        if ( decision.getVariable() != null && decision.getVariable().getTypeRef() != null ) {
            type = compiler.resolveTypeRef( model, dn, decision, decision.getVariable(), decision.getVariable().getTypeRef() );
        } else {
            type = compiler.resolveTypeRef( model, dn, decision, decision, null );
        }
        dn.setResultType( type );
        model.addDecision( dn );
    }
    @Override
    public boolean accept(DMNNode node) {
        return node instanceof DecisionNodeImpl;
    }
    @Override
    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        DecisionNodeImpl di = (DecisionNodeImpl) node;
        compiler.linkRequirements( model, di );

        ctx.enterFrame();
        try {
            for( DMNNode dep : di.getDependencies().values() ) {
                if( dep instanceof DecisionNode ) {
                    ctx.setVariable( dep.getName(), ((DecisionNode) dep).getResultType() );
                } else if( dep instanceof InputDataNode ) {
                    ctx.setVariable( dep.getName(), ((InputDataNode) dep).getType() );
                } else if( dep instanceof BusinessKnowledgeModelNode ) {
                    // might need to create a DMNType for "functions" and replace the type here by that
                    ctx.setVariable( dep.getName(), ((BusinessKnowledgeModelNode)dep).getResultType() );
                }
            }
            DMNExpressionEvaluator evaluator = compiler.getEvaluatorCompiler().compileExpression( ctx, model, di, di.getName(), di.getDecision().getExpression() );
            di.setEvaluator( evaluator );
        } finally {
            ctx.exitFrame();
        }
    }
}