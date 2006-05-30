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

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class ColumnHandler extends BaseAbstractHandler
    implements
    Handler {
    ColumnHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );
            this.validParents.add( OrDescr.class );
            this.validParents.add( NotDescr.class );
            this.validParents.add( ExistsDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( AndDescr.class );
            this.validPeers.add( OrDescr.class );
            this.validPeers.add( NotDescr.class );
            this.validPeers.add( ExistsDescr.class );
            this.validPeers.add( EvalDescr.class );
            this.validPeers.add( ColumnDescr.class );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                             attrs );

        final String objectType = attrs.getValue( "object-type" );

        if ( objectType == null || objectType.trim().equals( "" ) ) {
            throw new SAXParseException( "<column> requires an 'object-type' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        ColumnDescr columnDescr = null;

        final String identifier = attrs.getValue( "identifier" );
        if ( identifier == null || identifier.trim().equals( "" ) ) {
            columnDescr = new ColumnDescr( objectType );
        } else {
            columnDescr = new ColumnDescr( objectType,
                                           identifier );
        }

        return columnDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final ColumnDescr columnDescr = (ColumnDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        final Object parent = it.previous();

        final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
        parentDescr.addDescr( columnDescr );

        return null;
    }

    public Class generateNodeFor() {
        return ColumnDescr.class;
    }
}