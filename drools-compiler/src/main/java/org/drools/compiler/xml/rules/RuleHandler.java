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
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RuleHandler extends BaseAbstractHandler
    implements
    Handler {
    public RuleHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PackageDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FunctionDescr.class );
            this.validPeers.add( RuleDescr.class );
            this.validPeers.add( QueryDescr.class );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        final String ruleName = attrs.getValue( "name" );
        emptyAttributeCheck( localName,
                             "name",
                             ruleName,
                             parser );

        final RuleDescr ruleDescr = new RuleDescr( ruleName.trim() );

        return ruleDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final RuleDescr ruleDescr = (RuleDescr) parser.getCurrent();

        final AndDescr lhs = ruleDescr.getLhs();

        if ( lhs == null ) {
            throw new SAXParseException( "<rule> requires a LHS",
                                         parser.getLocator() );
        }

        NodeList list = element.getElementsByTagName( "rhs" );
        if ( list.getLength() == 0 ) {
            throw new SAXParseException( "<rule> requires a <rh> child element",
                                         parser.getLocator() );
        }

        ruleDescr.setConsequence( ((org.w3c.dom.Text)list.item( 0 ).getChildNodes().item( 0 )).getWholeText() );

        NodeList attributes = element.getElementsByTagName( "rule-attribute" );
        for ( int i = 0, length = attributes.getLength(); i < length; i++ ) {
            final String name = ((Element) attributes.item( i )).getAttribute( "name" );
            emptyAttributeCheck( "rule-attribute",
                                 "name",
                                 name,
                                 parser );

            final String value = ((Element) attributes.item( i )).getAttribute( "value" );

            ruleDescr.addAttribute( new AttributeDescr( name,
                                                        value ) );
        }

        ((PackageDescr) parser.getData()).addRule( ruleDescr );

        return ruleDescr;
    }

    public Class generateNodeFor() {
        return RuleDescr.class;
    }
}
