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

package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.List;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;
import org.mvel.MVEL;

/**
 * @author etirelli
 *
 */
public class MVELEvalBuilder
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

        try {
            final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                     null,
                                                                     context.getPkg().getGlobals() );
            factory.setNextFactory( ((MVELDialect) context.getDialect()).getClassImportResolverFactory() );

            Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                      evalDescr,
                                                                                      evalDescr.getContent() );
            final List[] usedIdentifiers = analysis.getBoundIdentifiers();

            final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
            for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
            }

            final EvalCondition eval = new EvalCondition( declarations );
            final Serializable expr = MVEL.compileExpression( (String) evalDescr.getContent(),
                                                              ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );

            eval.setEvalExpression( new MVELEvalExpression( expr,
                                                            factory ) );

            return eval;
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    evalDescr,
                                                    null,
                                                    "Unable to build expression for 'predicate' node '" + evalDescr.getContent() + "'" ) );
            return null;
        }
    }

}
