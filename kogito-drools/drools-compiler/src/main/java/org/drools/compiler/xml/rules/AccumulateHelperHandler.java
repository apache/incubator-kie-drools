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

package org.drools.compiler.xml.rules;

import java.util.HashSet;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 */

public class AccumulateHelperHandler extends BaseAbstractHandler
    implements
    Handler {

    public AccumulateHelperHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AccumulateDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.validPeers.add( PatternDescr.class );
            this.validPeers.add( BaseDescr.class );

            this.allowNesting = true;
        }
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

        
        
        final String expression =((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText();

        final Object parent = parser.getParent();

        final AccumulateDescr accumulate = (AccumulateDescr) parent;

        if ( localName.equals( "init" ) ) {
            emptyContentCheck( localName, expression, parser );
            accumulate.setInitCode( expression.trim() );
        } else if ( localName.equals( "action" ) ) {  
            emptyContentCheck( localName, expression, parser );
            accumulate.setActionCode( expression.trim() );
        } else if ( localName.equals( "result" ) ) { 
            emptyContentCheck( localName, expression, parser );
            accumulate.setResultCode( expression.trim() );
        } else if ( localName.equals( "reverse" ) ) {
            emptyContentCheck( localName, expression, parser );
            accumulate.setReverseCode( expression.trim() );
        } else if ( localName.equals( "external-function" ) ) {
            accumulate.setExternalFunction( true );
            accumulate.setFunctionIdentifier( element.getAttribute( "evaluator" ) );
            accumulate.setExpression( element.getAttribute( "expression" ) );
        }

        return null;
    }

    public Class generateNodeFor() {
        return BaseDescr.class;
    }    

}
