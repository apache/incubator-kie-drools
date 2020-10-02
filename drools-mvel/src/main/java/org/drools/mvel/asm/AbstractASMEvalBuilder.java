/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.EvalConditionFactory;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.DeclarationScopeResolver;

import static org.drools.compiler.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.compiler.rule.builder.PatternBuilder.createImplicitBindings;
import static org.drools.mvel.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.mvel.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.mvel.java.JavaRuleBuilderHelper.registerInvokerBytecode;

public abstract class AbstractASMEvalBuilder implements RuleConditionBuilder {
    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr) {
        // it must be an EvalDescr
        final EvalDescr evalDescr = (EvalDescr) descr;

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );

        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          evalDescr,
                                                                          evalDescr.getContent(),
                                                                          new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ),
                                                                                                context ) );

        List<Declaration> requiredDeclarations = new ArrayList<Declaration>();
        for (String usedIdentifier : analysis.getIdentifiers()) {
            Declaration usedDec = decls.get(usedIdentifier);
            if (usedDec != null) {
                requiredDeclarations.add(usedDec);
            }
        }

        final Declaration[] declarations = requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()]);
        return buildEval(context, evalDescr, analysis, declarations);
     }

    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        if (prefixPattern == null) {
            return build(context, descr);
        }

        EvalDescr evalDescr = (EvalDescr) descr;

        PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), evalDescr.getContent() );
        AnalysisResult analysis = buildAnalysis(context, prefixPattern, predicateDescr, null );

        Declaration[] declarations = getUsedDeclarations(context, prefixPattern, analysis);
        return buildEval(context, evalDescr, analysis, declarations);
    }

    private RuleConditionElement buildEval(RuleBuildContext context, EvalDescr evalDescr, AnalysisResult analysis, Declaration[] declarations) {
        String className = "eval" + context.getNextId();
        evalDescr.setClassMethodName( className );

        Arrays.sort(declarations, RuleTerminalNode.SortDeclarations.instance);

        EvalCondition eval = EvalConditionFactory.Factory.get().createEvalCondition(declarations);

        Map<String, Object> vars = createVariableContext( className,
                                                          (String)evalDescr.getContent(),
                                                          context,
                                                          declarations,
                                                          null,
                                                          analysis.getBoundIdentifiers().getGlobals() );

        generateMethodTemplate("evalMethod", context, vars);

        byte[] bytecode = createEvalBytecode(context, vars);
        registerInvokerBytecode(context, vars, bytecode, eval);
        return eval;
    }

    private Declaration[] getUsedDeclarations(RuleBuildContext context, Pattern pattern, AnalysisResult analysis) {
        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        final List<Declaration> declarations = new ArrayList<Declaration>();

        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            declarations.add( context.getDeclarationResolver().getDeclaration( id ) );
        }

        createImplicitBindings( context,
                                pattern,
                                analysis.getNotBoundedIdentifiers(),
                                analysis.getBoundIdentifiers(),
                                declarations );

        return declarations.toArray( new Declaration[declarations.size()] );
    }

    protected abstract byte[] createEvalBytecode(RuleBuildContext context, Map vars);
}
