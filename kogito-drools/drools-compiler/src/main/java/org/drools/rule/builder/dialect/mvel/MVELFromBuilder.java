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
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Pattern;
import org.drools.rule.ConditionalElement;
import org.drools.rule.From;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.spi.DataProvider;
import org.mvel.MVEL;

/**
 * A builder for "from" conditional element
 * 
 * @author etirelli
 */
public class MVELFromBuilder
    implements
    ConditionalElementBuilder,
    FromBuilder {

    /* (non-Javadoc)
     * @see org.drools.dialect.mvel.FromBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.PatternBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {
        final FromDescr fromDescr = (FromDescr) descr;

        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );
        
        final Pattern pattern = patternBuilder.build( context,
                                                      fromDescr.getReturnedPattern() );

        if ( pattern == null ) {
            return null;
        }

        final AccessorDescr accessor = (AccessorDescr) fromDescr.getDataSource();
        DataProvider dataProvider = null;
        try {
            //            JFDIParser parser = createParser( utils,
            //                                              accessor.toString() );
            final DroolsMVELFactory factory = new DroolsMVELFactory(context.getDeclarationResolver().getDeclarations(), null,  context.getPkg().getGlobals() );

            //parser.setValueHandlerFactory( factory );
            final Serializable compiled = MVEL.compileExpression( accessor.toString() );

            dataProvider = new MVELDataProvider( compiled,
                                                 factory );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    fromDescr,
                                                    null,
                                                    "Unable to build expression for 'from' node '" + accessor.toString() + "'" ) );
            return null;
        }

        return new From( pattern,
                         dataProvider );
    }
}
