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

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ConnectiveDescr;
import org.drools.lang.descr.ConnectiveDescr.RestrictionConnectiveType;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FieldConstraintHandler extends BaseAbstractHandler
    implements
    Handler {
    public FieldConstraintHandler() {
//        if ( (this.validParents == null) && (this.validPeers == null) ) {
//            this.validParents = new HashSet();
//            this.validParents.add( PatternDescr.class );
//            this.validParents.add( AndDescr.class );
//            this.validParents.add( OrDescr.class );
//
//            this.validPeers = new HashSet();
//            this.validPeers.add( null );
//            this.validPeers.add( FieldConstraintDescr.class );
//            this.validPeers.add( PredicateDescr.class );
//            this.validPeers.add( BindingDescr.class );
//
//            this.validPeers.add( AndDescr.class );
//            this.validPeers.add( OrDescr.class );
//
//            this.allowNesting = false;
//        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        final String fieldName = attrs.getValue( "field-name" );        
        emptyAttributeCheck( localName, "field-name", fieldName, parser );                        
        final ConnectiveDescr connective = new ConnectiveDescr(RestrictionConnectiveType.AND);
        connective.setParen( false );
        
        connective.setPrefix( fieldName );
        
        return connective;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final ConnectiveDescr c = (ConnectiveDescr) parser.getCurrent();
        
        Object p = parser.getParent( );
        if ( p instanceof PatternDescr ) {
            StringBuilder sb = new StringBuilder();
            c.buildExpression( sb );
                  
            ExprConstraintDescr expr = new ExprConstraintDescr( );
            expr.setExpression( sb.toString() );
              
            final PatternDescr patternDescr = (PatternDescr) parser.getParent( );  
            patternDescr.addConstraint( expr );        
            
        } else if ( p instanceof ConnectiveDescr ) {
            ((ConnectiveDescr) p).add( c );
        }

        return c;
    }

    public Class generateNodeFor() {
        return FieldConstraintDescr.class;
    }
}
