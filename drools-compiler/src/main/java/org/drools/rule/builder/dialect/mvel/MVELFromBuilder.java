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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.base.dataproviders.MVELDataProvider;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.MVELExprDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Declaration;
import org.drools.rule.From;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.KnowledgeHelper;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

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
        // This builder is re-usable in other dialects, so specify by name
        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
        boolean typeSafe = context.isTypesafe();
        if (!dialect.isStrictMode()) {
            context.setTypesafe(false);
        }

        final FromDescr fromDescr = (FromDescr) descr;

        final MVELExprDescr expr = (MVELExprDescr) fromDescr.getDataSource();
        From from = null;
        try {
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            String text = (String) expr.getText();
            AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                 descr,
                                                                 text,
                                                                 new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                      context.getPackageBuilder().getGlobals() ) );
            if ( analysis == null ) {
                // something bad happened
                return null;
            }
            
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();            
            final Declaration[] declarations =  new Declaration[usedIdentifiers.getDeclrClasses().size()];
            String[] declrStr = new String[declarations.length];
            int j = 0;
            for (String str : usedIdentifiers.getDeclrClasses().keySet() ) {
                declrStr[j] = str;
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
                                                                       false );

            MVELDataProvider dataProvider = new MVELDataProvider( unit,
                                                                  context.getDialect().getId() );
            from = new From( dataProvider );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( from,
                                  dataProvider );
            
            dataProvider.compile( data );
        } catch ( final Exception e ) {
            copyErrorLocation(e, fromDescr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          fromDescr,
                                                          null,
                                                          "Unable to build expression for 'from' : " + e.getMessage() + " '" + expr.getText() + "'" ) );
            return null;

        } finally {
            context.setTypesafe( typeSafe );
        }

        return from;
    }
}
