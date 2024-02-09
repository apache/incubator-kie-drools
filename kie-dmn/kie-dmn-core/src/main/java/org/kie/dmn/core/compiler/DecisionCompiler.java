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
package org.kie.dmn.core.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;

import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

public class DecisionCompiler implements DRGElementCompiler {
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof Decision;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        Decision decision = (Decision) de;
        DecisionNodeImpl dn = new DecisionNodeImpl( decision );
        DMNType type;
        if ( decision.getVariable() == null ) {
            DMNCompilerHelper.reportMissingVariable( model, de, decision, Msg.MISSING_VARIABLE_FOR_DECISION );
            return;
        }
        DMNCompilerHelper.checkVariableName( model, decision, decision.getName() );
        if ( decision.getVariable() != null && decision.getVariable().getTypeRef() != null ) {
            type = compiler.resolveTypeRef(model, decision, decision.getVariable(), decision.getVariable().getTypeRef());
        } else {
            type = compiler.resolveTypeRef(model, decision, decision, null);
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
            loadInCtx(di, ctx, model);
            DMNExpressionEvaluator evaluator = compiler.getEvaluatorCompiler().compileExpression( ctx, model, di, di.getName(), di.getDecision().getExpression() );
            di.setEvaluator( evaluator );
        } finally {
            ctx.exitFrame();
        }
    }

    public static void loadInCtx(DMNBaseNode node, DMNCompilerContext ctx, DMNModelImpl model) {
        Map<String, DMNType> importedTypes = new HashMap<>();
        for (DMNNode dep : node.getDependencies().values()) {
            DMNType depType = null;
            if (dep instanceof DecisionNode) {
                depType = ((DecisionNode) dep).getResultType();
            } else if (dep instanceof InputDataNode) {
                depType = ((InputDataNode) dep).getType();
            } else if (dep instanceof BusinessKnowledgeModelNode) {
                depType = ((BusinessKnowledgeModelNode) dep).getResultType();
            } else if (dep instanceof DecisionServiceNode) {
                depType = ((DecisionServiceNode) dep).getResultType();
            }
            if (dep.getModelNamespace().equals(model.getNamespace())) {
                // for BKMs might need to create a DMNType for "functions" and replace the type here by that
                ctx.setVariable(dep.getName(), depType);
            } else if (isInUnnamedImport(dep, model)) {
                // the dependency is an unnamed import
                ctx.setVariable(dep.getName(), depType);
            } else {
                // then the dependency is an imported dependency.
                Optional<String> alias = model.getImportAliasFor(dep.getModelNamespace(), dep.getModelName());
                if (alias.isPresent()) {
                    CompositeTypeImpl importedComposite = (CompositeTypeImpl) importedTypes.computeIfAbsent(alias.get(), a -> new CompositeTypeImpl());
                    importedComposite.addField(dep.getName(), depType);
                }
            }
        }
        for (Entry<String, DMNType> importedType : importedTypes.entrySet()) {
            ctx.setVariable(importedType.getKey(), importedType.getValue());
        }
    }

}