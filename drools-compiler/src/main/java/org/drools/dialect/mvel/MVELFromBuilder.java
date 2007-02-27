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

package org.drools.dialect.mvel;

import java.io.Serializable;

import org.drools.base.DroolsMVELFactory;
import org.drools.base.dataproviders.MVELDataProvider;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.From;
import org.drools.semantics.java.builder.BuildContext;
import org.drools.semantics.java.builder.BuildUtils;
import org.drools.semantics.java.builder.ColumnBuilder;
import org.drools.semantics.java.builder.ConditionalElementBuilder;
import org.drools.semantics.java.builder.FromBuilder;
import org.drools.spi.DataProvider;
import org.mvel.CompiledExpression;
import org.mvel.ExpressionParser;
import org.mvel.MVEL;

/**
 * A builder for "from" conditional element
 * 
 * @author etirelli
 */
public class MVELFromBuilder
    implements
    ConditionalElementBuilder, FromBuilder {

    /* (non-Javadoc)
     * @see org.drools.dialect.mvel.FromBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(final BuildContext context,
                                    final BuildUtils utils,
                                    final ColumnBuilder columnBuilder,
                                    final BaseDescr descr) {
        FromDescr fromDescr = (FromDescr) descr;

        final Column column = columnBuilder.build( context,
                                                   utils,
                                                   fromDescr.getReturnedColumn() );

        if ( column == null ) {
            return null;
        }

        AccessorDescr accessor = (AccessorDescr) fromDescr.getDataSource();
        DataProvider dataProvider = null;
        try {
//            JFDIParser parser = createParser( utils,
//                                              accessor.toString() );
            DroolsMVELFactory factory = new DroolsMVELFactory( );
            factory.setDeclarationMap( context.getDeclarationResolver().getDeclarations() );
            factory.setGlobalsMap( context.getPkg().getGlobals() );
            
            //parser.setValueHandlerFactory( factory );
            Serializable compiled = MVEL.compileExpression( accessor.toString() );

            dataProvider = new MVELDataProvider( compiled,
                                                 factory );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    fromDescr,
                                                    null,
                                                    "Unable to build expression for 'from' node '" + accessor.toString() + "'" ) );
            return null;
        }

        return new From( column,
                         dataProvider );
    }
}
