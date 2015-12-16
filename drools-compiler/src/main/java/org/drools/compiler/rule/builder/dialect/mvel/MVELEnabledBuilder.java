/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELEnabledExpression;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.definitions.rule.impl.RuleImpl.SafeEnabled;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.internal.security.KiePolicyHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MVELEnabledBuilder
    implements
    EnabledBuilder {

    public void build(RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getDeclarationResolver().pushOnBuildStack( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Class< ? >> otherVars = new HashMap<String, Class< ? >>();
            otherVars.put( "rule",
                           RuleImpl.class );

            Map<String, Declaration> declrs = context.getDeclarationResolver().getDeclarations( context.getRule() );

            AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                 context.getRuleDescr(),
                                                                 context.getRuleDescr().getEnabled(),
                                                                 new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( declrs ),
                                                                                       context.getKnowledgeBuilder().getGlobals() ),
                                                                 otherVars );

            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = declrs.get( id );
            }
            Arrays.sort( previousDeclarations, SortDeclarations.instance  );            

            String exprStr = context.getRuleDescr().getEnabled();
            exprStr = exprStr.substring( 1,
                                         exprStr.length() - 1 ) + " ";
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( exprStr,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       otherVars,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false,
                                                                       MVELCompilationUnit.Scope.EXPRESSION );

            MVELEnabledExpression expr = new MVELEnabledExpression( unit,
                                                                    dialect.getId() );
            context.getRule().setEnabled( KiePolicyHelper.isPolicyEnabled() ? new SafeEnabled(expr) : expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );

            expr.compile( data, context.getRule() );
        } catch ( final Exception e ) {
            DialectUtil.copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'enabled' : " + e.getMessage() + " '" + context.getRuleDescr().getEnabled() + "'" ) );
        }
    }

}
