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

import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
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
    PackageHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( null );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final String ruleSetName = attrs.getValue( "name" );

        if ( ruleSetName == null || ruleSetName.trim().equals( "" ) ) {
            throw new SAXParseException( "<package> requires a 'name' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        final PackageDescr packageDescr = new PackageDescr( ruleSetName.trim() );

        this.xmlPackageReader.setPackageDescr( packageDescr );
        return packageDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final PackageDescr packageDescr = this.xmlPackageReader.getPackageDescr();
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final Configuration[] imports = config.getChildren( "import" );

        for ( int i = 0, length = imports.length; i < length; i++ ) {
            final String importEntry = imports[i].getAttribute( "name" );

            if ( importEntry == null || importEntry.trim().equals( "" ) ) {
                throw new SAXParseException( "<import> cannot be blank",
                                             this.xmlPackageReader.getLocator() );
            }
            packageDescr.addImport( new ImportDescr( importEntry ) );
        }
        
        final Configuration[] importfunctions = config.getChildren( "importfunction" );

        for ( int i = 0, length = importfunctions.length; i < length; i++ ) {
            final String importfunctionEntry = importfunctions[i].getAttribute( "name" );

            if ( importfunctionEntry == null || importfunctionEntry.trim().equals( "" ) ) {
                throw new SAXParseException( "<importfunction> cannot be blank",
                                             this.xmlPackageReader.getLocator() );
            }
            
            FunctionImportDescr funcdescr = new FunctionImportDescr();
            funcdescr.setTarget( importfunctionEntry );
            
            packageDescr.addFunctionImport(funcdescr);
        }
        

        final Configuration[] globals = config.getChildren( "global" );

        for ( int i = 0, length = globals.length; i < length; i++ ) {
            final String identifier = globals[i].getAttribute( "identifier" );

            if ( identifier == null || identifier.trim().equals( "" ) ) {
                throw new SAXParseException( "<global> must have an identifier",
                                             this.xmlPackageReader.getLocator() );
            }

            final String type = globals[i].getAttribute( "type" );
            if ( type == null || type.trim().equals( "" ) ) {
                throw new SAXParseException( "<global> must have specify a type",
                                             this.xmlPackageReader.getLocator() );
            }
            final GlobalDescr global = new GlobalDescr( identifier,
                                                        type );
            packageDescr.addGlobal( global );
        }

        return null;
    }

    public Class generateNodeFor() {
        return PackageDescr.class;
    }
}
