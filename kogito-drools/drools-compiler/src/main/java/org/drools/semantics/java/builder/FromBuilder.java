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

package org.drools.semantics.java.builder;

import java.io.IOException;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.codehaus.jfdi.parser.JFDILexer;
import org.codehaus.jfdi.parser.JFDIParser;
import org.drools.base.DroolsJFDIFactory;
import org.drools.base.dataproviders.JFDIDataProvider;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.From;
import org.drools.spi.DataProvider;

/**
 * A builder for "from" conditional element
 * 
 * @author etirelli
 */
public class FromBuilder
    implements
    ConditionalElementBuilder {

    /**
     * @inheritDoc
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
            JFDIParser parser = createParser( utils,
                                              accessor.toString() );
            DroolsJFDIFactory factory = new DroolsJFDIFactory( utils.getTypeResolver() );
            factory.setDeclarationMap( context.getDeclarationResolver().getDeclarations() );
            factory.setGlobalsMap( context.getPkg().getGlobals() );
            parser.setValueHandlerFactory( factory );

            dataProvider = new JFDIDataProvider( parser.expr(),
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

    protected JFDIParser createParser(BuildUtils utils,
                                      String text) throws IOException {
        JFDIParser parser = new JFDIParser( createTokenStream( text ) );
        DroolsJFDIFactory factory = new DroolsJFDIFactory( utils.getTypeResolver() );
        parser.setValueHandlerFactory( factory );
        return parser;
    }

    private TokenStream createTokenStream(String text) throws IOException {
        return new CommonTokenStream( createLexer( text ) );
    }

    private JFDILexer createLexer(String text) throws IOException {
        JFDILexer lexer = new JFDILexer( new ANTLRStringStream( text ) );
        return lexer;
    }

}
