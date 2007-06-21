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
import org.xml.sax.SAXParseException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class PatternHandler extends BaseAbstractHandler
    implements
    Handler {
    PatternHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );
            this.validParents.add( OrDescr.class );
            this.validParents.add( NotDescr.class );
            this.validParents.add( ExistsDescr.class );
            this.validParents.add( CollectDescr.class );

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

        final String objectType = attrs.getValue( "object-type" );

        if ( objectType == null || objectType.trim().equals( "" ) ) {
            throw new SAXParseException( "<pattern> requires an 'object-type' attribute",
                                         this.xmlPackageReader.getLocator() );
        }

        PatternDescr patternDescr = null;

        final String identifier = attrs.getValue( "identifier" );
        if ( identifier == null || identifier.trim().equals( "" ) ) {
            patternDescr = new PatternDescr( objectType );
        } else {
            patternDescr = new PatternDescr( objectType,
                                           identifier );
        }

        return patternDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final PatternDescr patternDescr = (PatternDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();

        if ( parent.getClass().getName().equals( CollectDescr.class.getName() ) ) {
            final CollectDescr parentDescr = (CollectDescr) parent;
            parentDescr.setResultPattern( patternDescr );
        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            List conditionalDescriptors = parentDescr.getDescrs();
            
            if ( !conditionalDescriptors.isEmpty() ) {
                for ( Iterator iterator = conditionalDescriptors.iterator(); iterator.hasNext(); ) {
                    Object obj = iterator.next();
                    
                    if ( obj.getClass().getName().intern().equals( CollectDescr.class.getName().intern() )) {
                        return null;
                    }
                }
            }
            parentDescr.addDescr( patternDescr );            
        }
        return null;
    }

    public Class generateNodeFor() {
        return PatternDescr.class;
    }
}