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
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.v1_1.BusinessKnowledgeModel;
import org.kie.dmn.model.v1_1.DRGElement;
import org.kie.dmn.model.v1_1.FunctionDefinition;

public class BusinessKnowledgeModelCompiler implements DRGElementCompiler {
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof BusinessKnowledgeModel;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) de;
        BusinessKnowledgeModelNodeImpl bkmn = new BusinessKnowledgeModelNodeImpl( bkm );
        DMNType type = null;
        if ( bkm.getVariable() == null ) {
            DMNCompilerHelper.reportMissingVariable( model, de, bkm, Msg.MISSING_VARIABLE_FOR_BKM );
            return;
        }
        DMNCompilerHelper.checkVariableName( model, bkm, bkm.getName() );
        if ( bkm.getVariable() != null && bkm.getVariable().getTypeRef() != null ) {
            type = compiler.resolveTypeRef( model, bkmn, bkm, bkm.getVariable(), bkm.getVariable().getTypeRef() );
        } else {
            // for now the call bellow will return type UNKNOWN
            type = compiler.resolveTypeRef( model, bkmn, bkm, bkm, null );
        }
        bkmn.setResultType( type );
        model.addBusinessKnowledgeModel( bkmn );
    }
    @Override
    public boolean accept(DMNNode node) {
        return node instanceof BusinessKnowledgeModelNodeImpl;
    }
    @Override
    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) node;
        compiler.linkRequirements( model, bkmi );

        ctx.enterFrame();
        try {
            for( DMNNode dep : bkmi.getDependencies().values() ) {
                if( dep instanceof BusinessKnowledgeModelNode ) {
                    // might need to create a DMNType for "functions" and replace the type here by that
                    ctx.setVariable( dep.getName(), ((BusinessKnowledgeModelNode)dep).getResultType() );
                }
            }
            // to allow recursive call from inside a BKM node, a variable for self must be available for the compiler context:
            ctx.setVariable(bkmi.getName(), bkmi.getResultType());
            FunctionDefinition funcDef = bkmi.getBusinessKnowledModel().getEncapsulatedLogic();
            DMNExpressionEvaluator exprEvaluator = compiler.getEvaluatorCompiler().compileExpression( ctx, model, bkmi, bkmi.getName(), funcDef );
            bkmi.setEvaluator( exprEvaluator );
        } finally {
            ctx.exitFrame();
        }
    }
}