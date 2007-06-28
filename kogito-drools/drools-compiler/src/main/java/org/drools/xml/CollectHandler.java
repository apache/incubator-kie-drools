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

import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 *
 */
public class CollectHandler extends BaseAbstractHandler
    implements
    Handler {

    CollectHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();

            this.validParents.add( FromDescr.class );

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
        final CollectDescr collectDescr = new CollectDescr();
        return collectDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {

        final Configuration config = this.xmlPackageReader.endConfiguration();
        final CollectDescr collectDescr = (CollectDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();

        if ( parent.getClass().getName().equals( FromDescr.class.getName() ) ) {
            final PatternDescr resultPattern = (PatternDescr) ite.previous();
            resultPattern.setSource( collectDescr );
        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( collectDescr );
        }

        return null;
    }

    public Class generateNodeFor() {
        return CollectDescr.class;
    }

}
