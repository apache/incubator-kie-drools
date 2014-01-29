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

package org.drools.compiler.rule.builder.dialect.mvel;

import static org.drools.compiler.rule.builder.dialect.DialectUtil.copyErrorLocation;

import java.util.Arrays;
import java.util.Map;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELEvalExpression;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.EvalCondition.SafeEvalExpression;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.internal.security.KiePolicyHelper;

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
        boolean typesafe = context.isTypesafe();
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

            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = decls.get( id );
            }
            Arrays.sort( previousDeclarations, SortDeclarations.instance  ); 
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( (String) evalDescr.getContent(),
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false );
            final EvalCondition eval = new EvalCondition( previousDeclarations );

            MVELEvalExpression expr = new MVELEvalExpression( unit,
                                                              dialect.getId() );
            eval.setEvalExpression( KiePolicyHelper.isPolicyEnabled() ? new SafeEvalExpression(expr) : expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( eval,
                                  expr );

            expr.compile( data );
            return eval;
        } catch ( final Exception e ) {
            copyErrorLocation(e, evalDescr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          evalDescr,
                                                          e,
                                                          "Unable to build expression for 'eval':" + e.getMessage() + " '" + evalDescr.getContent() + "'" ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
