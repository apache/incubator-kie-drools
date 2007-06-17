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
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class AndHandler extends BaseAbstractHandler
    implements
    Handler {
    AndHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( QueryDescr.class );
            this.validParents.add( RuleDescr.class );
            this.validParents.add( OrDescr.class );
            this.validParents.add( AndDescr.class );
            this.validParents.add( LiteralRestrictionHandler.class );


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
        final AndDescr andDescr = new AndDescr();

        return andDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final AndDescr andDescr = (AndDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        final Object parent = it.previous();
        
        
        if (parent instanceof RuleDescr || parent instanceof QueryDescr) {            
            final RuleDescr ruleDescr = (RuleDescr) parent;
            ruleDescr.setLhs( andDescr );
        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr ceDescr = (ConditionalElementDescr) parent;
            ceDescr.addDescr( andDescr );
        }
        
        return null;
    }

    public Class generateNodeFor() {
        return AndDescr.class;
    }
}