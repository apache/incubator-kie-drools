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
package org.drools.mvel.builder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.From;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.dataproviders.MVELDataProvider;
import org.drools.mvel.expr.MVELCompilationUnit;

/**
 * A builder for "from" conditional element
 */
public class MVELFromBuilder
    implements
        FromBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {
        String text = ((FromDescr) descr).getExpression();
        Optional<EntryPointId> entryPointId = context.getEntryPointId(text);
        if (entryPointId.isPresent()) {
            return entryPointId.get();
        }

        // This builder is re-usable in other dialects, so specify by name
        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
        boolean typeSafe = context.isTypesafe();
        if (!dialect.isStrictMode()) {
            context.setTypesafe(false);
        }

        try {
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                 descr,
                                                                 text,
                                                                 new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ),
                                                                                       context ) );
            if ( analysis == null ) {
                // something bad happened
                return null;
            }

            Class<?> returnType = analysis.getReturnType();
            if ( prefixPattern != null && !prefixPattern.isCompatibleWithFromReturnType( returnType ) ) {
                context.addError( new DescrBuildError( descr,
                                                       context.getRuleDescr(),
                                                       null,
                                                       "Pattern of type: '" + prefixPattern.getObjectType() + "' on rule '" + context.getRuleDescr().getName() +
                                                       "' is not compatible with type " + returnType.getCanonicalName() + " returned by source") );
                return null;

            }
            
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();            
            final Declaration[] declarations =  new Declaration[usedIdentifiers.getDeclrClasses().size()];
            int j = 0;
            for (String str : usedIdentifiers.getDeclrClasses().keySet() ) {
                declarations[j++] = decls.get( str );
            }
            Arrays.sort( declarations, SortDeclarations.instance  );            
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       declarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false,
                                                                       MVELCompilationUnit.Scope.CONSEQUENCE );

            MVELDataProvider dataProvider = new MVELDataProvider( unit, context.getDialect().getId() );

            From from = new From( dataProvider );
            from.setResultPattern( prefixPattern );

            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( from, dataProvider );
            
            dataProvider.compile( data, context.getRule() );
            return from;

        } catch ( final Exception e ) {
            AsmUtil.copyErrorLocation(e, descr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   descr,
                                                   null,
                                                   "Unable to build expression for 'from' : " + e.getMessage() + " '" + text + "'" ) );
            return null;

        } finally {
            context.setTypesafe( typeSafe );
        }
    }
}
