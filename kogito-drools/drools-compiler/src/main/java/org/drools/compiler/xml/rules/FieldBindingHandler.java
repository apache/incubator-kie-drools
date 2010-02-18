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

import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author mproctor
 */
public class FieldBindingHandler extends BaseAbstractHandler
    implements
    Handler {
    public FieldBindingHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PatternDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FieldConstraintDescr.class );
            this.validPeers.add( PredicateDescr.class );
            this.validPeers.add( FieldBindingDescr.class );
            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        
        final String identifier = attrs.getValue( "identifier" );
        final String fieldName = attrs.getValue( "field-name" );
        
        emptyAttributeCheck( localName, "identifier", identifier, parser );        
        emptyAttributeCheck( localName, "fieldName", fieldName, parser );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( fieldName,
                                                                           identifier );

        return fieldBindingDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) parser.getCurrent();

        final PatternDescr patternDescr = (PatternDescr) parser.getParent( );

        patternDescr.addConstraint( fieldBindingDescr );

        return fieldBindingDescr;
    }

    public Class generateNodeFor() {
        return FieldBindingDescr.class;
    }
}