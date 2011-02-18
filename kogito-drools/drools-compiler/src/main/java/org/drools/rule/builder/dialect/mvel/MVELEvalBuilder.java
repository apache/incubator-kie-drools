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

import java.util.Collection;
import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;

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
            MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());
            
            AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                              evalDescr,
                                                                              evalDescr.getContent(),
                                                                              new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                   context.getPackageBuilder().getGlobals() ) );

            Collection<Declaration> col = decls.values();
            Declaration[] previousDeclarations = (Declaration[]) col.toArray( new Declaration[col.size()] );
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( (String) evalDescr.getContent(),
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context );
            final EvalCondition eval = new EvalCondition( previousDeclarations );

            MVELEvalExpression expr = new MVELEvalExpression( unit,
                                                              dialect.getId() );
            eval.setEvalExpression( expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( context.getDialect().getId() );
            data.addCompileable( eval,
                                  expr );

            expr.compile( context.getPackageBuilder().getRootClassLoader() );
            return eval;
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          evalDescr,
                                                          e,
                                                          "Unable to build expression for 'eval':" + e.getMessage() + " '" + evalDescr.getContent() + "'" ) );
            return null;
        }
    }

}
