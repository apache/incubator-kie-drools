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
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class RestrictionConnectiveHandler extends BaseAbstractHandler
    implements
    Handler {
    RestrictionConnectiveHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( FieldConstraintDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( LiteralRestrictionDescr.class );
            this.validPeers.add( ReturnValueRestrictionDescr.class );
            this.validPeers.add( VariableRestrictionDescr.class );
            this.validPeers.add( RestrictionConnectiveDescr.class );
            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final String connective = attrs.getValue( "connective" );
        if ( connective == null || connective.trim().equals( "" ) ) {
            throw new SAXParseException( "<restriction-connective> requires a 'connective' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        RestrictionConnectiveDescr connectiveDescr = null;
        if ( connective.equals( "or" ) ) {
            connectiveDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.OR );
        } else if (connective.equals( "and" )) {
            connectiveDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.AND );
        } else {
            throw new SAXParseException( "<connective-restriction> requires a valid 'connective' value (and,or): " + connective,
                                         this.xmlPackageReader.getLocator() );
        }

        return connectiveDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final RestrictionConnectiveDescr connectiveDescr = (RestrictionConnectiveDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        final FieldConstraintDescr fieldConstriantDescr = (FieldConstraintDescr) it.previous();

        fieldConstriantDescr.addRestriction( connectiveDescr );

        return null;
    }

    public Class generateNodeFor() {
        return RestrictionConnectiveDescr.class;
    }
}