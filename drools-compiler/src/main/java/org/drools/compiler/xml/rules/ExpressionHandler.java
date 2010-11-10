package org.drools.compiler.xml.rules;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.HashSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.drools.lang.DRL5xLexer;
import org.drools.lang.DRL5xParser;
import org.drools.lang.DescrBuilderTree5x;
import org.drools.lang.DroolsTreeAdaptor;
import org.drools.lang.DescrBuilderTree5x.from_source_clause_return;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author fernandomeyer
 */

public class ExpressionHandler extends BaseAbstractHandler
    implements
    Handler {

    public ExpressionHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( FromHandler.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( BaseDescr.class );

            this.allowNesting = true;
        }
    }

    public Class generateNodeFor() {
        return BaseDescr.class;
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        return new BaseDescr();
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final String expression =((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText() + ";";
        
        emptyContentCheck( localName, expression, parser );

        FromDescr parent = (FromDescr) parser.getParent();

        final CharStream charStream = new ANTLRStringStream( expression.trim() );
        final DRL5xLexer lexer = new DRL5xLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRL5xParser drlParser = new DRL5xParser( tokenStream );
        drlParser.setTreeAdaptor(new DroolsTreeAdaptor());

        try {
        	Tree fromSourceTree = (Tree) drlParser.from_source().getTree();
        	if (!drlParser.hasErrors()){
				CommonTreeNodeStream nodes = new CommonTreeNodeStream(fromSourceTree);
				nodes.setTokenStream(tokenStream);
            	DescrBuilderTree5x walker = new DescrBuilderTree5x(nodes);
            	from_source_clause_return fromReturn = walker.from_source_clause();
            	parent.setDataSource(fromReturn.retAccessorDescr);
            	parent = fromReturn.fromDescr;
        	} else {
                throw new SAXParseException( "<" + localName + "> must have a valid expression content ",
                        parser.getLocator() );        		
        	}
        } catch ( final RecognitionException e ) {
            throw new SAXParseException( "<" + localName + "> must have a valid expression content ",
                                         parser.getLocator() );
        }

        return null;
    }
}