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

import org.drools.base.dataproviders.MVELDataProvider;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.compiler.Dialect;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.rule.From;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.DataProvider;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.integration.impl.ClassImportResolverFactory;

/**
 * A builder for "from" conditional element
 * 
 * @author etirelli
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
        DataProvider dataProvider = null;
        try {
            final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                     null,
                                                                     context.getPkg().getGlobals() );

            // This builder is re-usable in other dialects, so specify by name
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            String text = (String) accessor.toString();
            Dialect.AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                         descr,
                                                                         text );

            final Serializable expr = dialect.compile( text,
                                                       analysis,
                                                       null,
                                                       null,
                                                       context );

            dataProvider = new MVELDataProvider( expr,
                                                 factory );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    fromDescr,
                                                    null,
                                                    "Unable to build expression for 'from' node '" + accessor + "'" ) );
            return null;
        }

        return new From( dataProvider );
    }
}
