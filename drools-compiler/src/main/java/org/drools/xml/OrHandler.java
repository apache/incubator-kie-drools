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

import org.drools.lang.descr.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author mproctor
 */
class OrHandler extends BaseAbstractHandler
    implements
    Handler {
    OrHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );
            this.validParents.add( PatternDescr.class );
            
            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( AndDescr.class );
            this.validPeers.add( OrDescr.class );
            this.validPeers.add( NotDescr.class );
            this.validPeers.add( ExistsDescr.class );
            this.validPeers.add( EvalDescr.class );
            this.validPeers.add( PatternDescr.class );

            
            this.allowNesting = true;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        final OrDescr orDescr = new OrDescr();

        return orDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final OrDescr orDescr = (OrDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        final Object parent = it.previous();
        
        if ( parent instanceof ConditionalElementDescr )  { 
	        final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
	        parentDescr.addDescr( orDescr );
        } else if ( parent instanceof PatternDescr ) {
            final PatternDescr parentDescr = (PatternDescr) parent;
            parentDescr.addConstraint( orDescr );
        }
        
        return null;
    }

    public Class generateNodeFor() {
        return OrDescr.class;
    }
}