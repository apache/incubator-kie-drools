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

import org.drools.lang.descr.PackageDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class PackageHandler extends BaseAbstractHandler
    implements
    Handler {
    PackageHandler(XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( null );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                             attrs );

        String ruleSetName = attrs.getValue( "name" );

        if ( ruleSetName == null || ruleSetName.trim().equals( "" ) ) {
            throw new SAXParseException( "<package> requires a 'name' attribute",
                                         xmlPackageReader.getLocator() );
        }

        PackageDescr packageDescr = new PackageDescr( ruleSetName.trim() );

        xmlPackageReader.setPackageDescr( packageDescr );
        return packageDescr;
    }

    public Object end(String uri,
                      String localName) throws SAXException {
        PackageDescr packageDescr = this.xmlPackageReader.getPackageDescr();
        Configuration config = xmlPackageReader.endConfiguration();

        Configuration[] imports = config.getChildren( "import" );

        for ( int i = 0, length = imports.length; i < length; i++ ) {
            String importEntry = imports[i].getAttribute( "name" );

            if ( importEntry == null || importEntry.trim().equals( "" ) ) {
                throw new SAXParseException( "<import> cannot be blank",
                                             xmlPackageReader.getLocator() );
            }
            packageDescr.addImport( importEntry );
        }

        Configuration[] globals = config.getChildren( "global" );

        for ( int i = 0, length = globals.length; i < length; i++ ) {
            String identifier = globals[i].getAttribute( "identifier" );

            if ( identifier == null || identifier.trim().equals( "" ) ) {
                throw new SAXParseException( "<global> must have an identifier",
                                             xmlPackageReader.getLocator() );
            }

            String type = globals[i].getAttribute( "type" );
            if ( type == null || type.trim().equals( "" ) ) {
                throw new SAXParseException( "<global> must have specify a type",
                                             xmlPackageReader.getLocator() );
            }
            packageDescr.addGlobal( identifier,
                                    type );
        }

        return null;
    }

    public Class generateNodeFor() {
        return PackageDescr.class;
    }
}
