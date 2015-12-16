/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler.xml.rules;

import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FunctionHandler extends BaseAbstractHandler
    implements
    Handler {
    public FunctionHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        final String name = attrs.getValue( "name" );
        final String returnType = attrs.getValue( "return-type" );
        
        emptyAttributeCheck( localName, "name", name, parser );
        emptyAttributeCheck( localName, "return-type", returnType, parser );

        return new FunctionDescr( name, returnType );
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        FunctionDescr functionDescr = ( FunctionDescr ) parser.getCurrent();

        final NodeList parameters = element.getElementsByTagName( "parameter" );

        for ( int i = 0, length = parameters.getLength(); i < length; i++ ) {
            final String identifier = ((Element)parameters.item( i )).getAttribute( "identifier" );
            final String type = ((Element)parameters.item( i )).getAttribute( "type" );
            
            emptyAttributeCheck("parameter", "identifier", identifier, parser);
            emptyAttributeCheck("parameter", "type", type, parser);
            
            functionDescr.addParameter( type,
                                        identifier );
        }

        // we allow empty, "", bodies - but make sure that we atleast have a body element
              
        NodeList list = element.getElementsByTagName( "body" );
        if ( list.getLength() == 0 ) {
            throw new SAXParseException( "function must have a <body>",
                                         parser.getLocator() );

        }

        
        
        functionDescr.setText( ((org.w3c.dom.Text)list.item( 0 ).getChildNodes().item( 0 )).getWholeText() );

        final PackageDescr packageDescr = (PackageDescr) parser.getData();

        packageDescr.addFunction( functionDescr );

        return functionDescr;
    }

    public Class generateNodeFor() {
        return FunctionDescr.class;
    }
}
