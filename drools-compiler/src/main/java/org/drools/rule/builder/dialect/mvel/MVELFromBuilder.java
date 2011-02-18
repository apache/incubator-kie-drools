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
import java.util.Set;

import org.drools.base.dataproviders.MVELDataProvider;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.rule.Declaration;
import org.drools.rule.From;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.RuleBuildContext;

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
        final FromDescr fromDescr = (FromDescr) descr;

        final AccessorDescr accessor = (AccessorDescr) fromDescr.getDataSource();
        From from = null;
        try {
            // This builder is re-usable in other dialects, so specify by name
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            String text = (String) accessor.toString();
            AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                 descr,
                                                                 text,
                                                                 new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                      context.getPackageBuilder().getGlobals() ) );
            if ( analysis == null ) {
                // something bad happened
                return null;
            }
            Collection<Declaration> col = decls.values();
            Declaration[] previousDeclarations = (Declaration[]) col.toArray( new Declaration[col.size()] );
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context );

            MVELDataProvider dataProvider = new MVELDataProvider( unit,
                                                                  context.getDialect().getId() );
            from = new From( dataProvider );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );
            data.addCompileable( from,
                                  dataProvider );
            
            dataProvider.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          fromDescr,
                                                          null,
                                                          "Unable to build expression for 'from' : " + e.getMessage() + " '" + accessor + "'" ) );
            return null;
        }

        return from;
    }
}
