/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELObjectExpression;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.spi.KnowledgeHelper;

import java.util.Arrays;
import java.util.Map;

public class MVELObjectExpressionBuilder {

    private MVELObjectExpressionBuilder() { }

    public static MVELObjectExpression build( String expression, RuleBuildContext context ) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                           context.getRuleDescr(),
                                                                                           expression,
                                                                                           new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                                context.getKnowledgeBuilder().getGlobals() ) );
            context.setTypesafe( analysis.isTypesafe() );
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = decls.get( id );
            }
            Arrays.sort(previousDeclarations, RuleTerminalNode.SortDeclarations.instance);

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( expression,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false,
                                                                       MVELCompilationUnit.Scope.EXPRESSION );

            MVELObjectExpression expr = new MVELObjectExpression( unit,
                                                                      dialect.getId() );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );

            expr.compile( data );
            return expr;
        } catch ( final Exception e ) {
            DialectUtil.copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression : " + e.getMessage() + "'" + expression + "'" ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
