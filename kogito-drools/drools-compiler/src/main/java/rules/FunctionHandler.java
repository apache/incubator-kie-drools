package org.drools.xml.rules;

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
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FunctionHandler extends BaseAbstractHandler
    implements
    Handler {
    public FunctionHandler() {
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
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                             attrs );
        final String name = attrs.getValue( "name" );
        final String returnType = attrs.getValue( "return-type" );
        
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );
        emptyAttributeCheck( localName, "return-type", returnType, xmlPackageReader );

        final FunctionDescr functionDescr = new FunctionDescr( name,
                                                               returnType );
        
        return functionDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Configuration config = xmlPackageReader.endConfiguration();

        FunctionDescr functionDescr = ( FunctionDescr ) xmlPackageReader.getCurrent();

        final Configuration[] parameters = config.getChildren( "parameter" );

        for ( int i = 0, length = parameters.length; i < length; i++ ) {
            final String identifier = parameters[i].getAttribute( "identifier" );      
            final String type = parameters[i].getAttribute( "type" );
            
            emptyAttributeCheck("parameter", "identifier", identifier, xmlPackageReader);                  
            emptyAttributeCheck("parameter", "type", type, xmlPackageReader);
            
            functionDescr.addParameter( type,
                                        identifier );
        }

        // we allow empty, "", bodies - but make sure that we atleast have a body element
        final Configuration body = config.getChild( "body" );
        if ( body == null ) {
            throw new SAXParseException( "function must have a <body>",
                                         xmlPackageReader.getLocator() );
        }

        functionDescr.setText( body.getText() );

        final PackageDescr packageDescr = (PackageDescr) xmlPackageReader.getData();

        packageDescr.addFunction( functionDescr );

        return functionDescr;
    }

    public Class generateNodeFor() {
        return FunctionDescr.class;
    }
}