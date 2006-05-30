package org.drools.xml;

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

import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class LhsHandler extends BaseAbstractHandler
    implements
    Handler {
    LhsHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PackageDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FunctionDescr.class );
            this.validPeers.add( RuleDescr.class );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                             attrs );
        return null;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final String name = config.getAttribute( "name" );
        if ( name == null || name.trim().equals( "" ) ) {
            throw new SAXParseException( "<function> requires a 'name' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        final String returnType = config.getAttribute( "return-type" );
        if ( returnType == null || returnType.trim().equals( "" ) ) {
            throw new SAXParseException( "<function> requires a 'return-type' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        final FunctionDescr functionDescr = new FunctionDescr( name,
                                                         returnType );

        final Configuration[] parameters = config.getChildren( "parameter" );

        for ( int i = 0, length = parameters.length; i < length; i++ ) {
            final String identifier = parameters[i].getAttribute( "identifier" );
            if ( name == null || identifier.trim().equals( "" ) ) {
                throw new SAXParseException( "function's <parameter> requires an 'identifier' attribute",
                                             this.xmlPackageReader.getLocator() );
            }

            final String type = parameters[i].getText();
            if ( type == null || type.trim().equals( "" ) ) {
                throw new SAXParseException( "function's <parameter> must specify a 'type'",
                                             this.xmlPackageReader.getLocator() );
            }

            functionDescr.addParameter( type,
                                        identifier );
        }

        // we allow empty, "", bodies - but make sure that we atleast have a body element
        final Configuration body = config.getChild( "body" );
        if ( body == null ) {
            throw new SAXParseException( "function must have a <body>",
                                         this.xmlPackageReader.getLocator() );
        }

        functionDescr.setText( body.getText() );

        final PackageDescr packageDescr = this.xmlPackageReader.getPackageDescr();

        packageDescr.addFunction( functionDescr );

        return functionDescr;
    }

    public Class generateNodeFor() {
        return FunctionDescr.class;
    }
}