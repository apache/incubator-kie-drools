/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.rule.builder.dialect.java;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;

public class JavaEvalBuilder extends AbstractJavaRuleBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    /**
     * Builds and returns an Eval Conditional Element
     * 
     * @param context The current build context
     * @param utils The current build utils instance
     * @param patternBuilder not used by EvalBuilder
     * @param descr The Eval Descriptor to build the eval conditional element from
     * 
     * @return the Eval Conditional Element
     */
    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {
        // it must be an EvalDescr
        final EvalDescr evalDescr = (EvalDescr) descr;

        final String className = "eval" + context.getNextId();

        evalDescr.setClassMethodName( className );
        
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                  evalDescr,
                                                                                  evalDescr.getContent(),
                                                                                  new BoundIdentifiers( context.getDeclarationResolver().getDeclarationClasses( decls ),                 
                                                                                                        context.getPackageBuilder().getGlobals() ) );
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final Declaration[] declarations = decls.values().toArray( new Declaration[decls.size()]);
        Arrays.sort( declarations, SortDeclarations.isntance  );
        
        final EvalCondition eval = new EvalCondition( declarations );

        final Map map = createVariableContext( className,
                                               (String) evalDescr.getContent(),
                                               context,
                                               declarations,
                                               null,
                                               usedIdentifiers.getGlobals(),
                                               null );

        generatTemplates( "evalMethod",
                          "evalInvoker",
                          context,
                          className,
                          map,
                          eval,
                          descr );

        return eval;
    }
}
