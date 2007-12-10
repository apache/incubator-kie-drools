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
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 */
public class AccumulateHandler extends BaseAbstractHandler
    implements
    Handler {

    public AccumulateHandler() {
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
                        final Attributes attrs,
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {

        xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        final AccumulateDescr accumulateDesrc = new AccumulateDescr();
        return accumulateDesrc;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {

        final Configuration config = xmlPackageReader.endConfiguration();
        final AccumulateDescr accumulateDescr = (AccumulateDescr) xmlPackageReader.getCurrent();

        final Object parent = xmlPackageReader.getParent();

        if ( parent.getClass().getName().equals( FromDescr.class.getName() ) ) {
            final PatternDescr result = (PatternDescr) xmlPackageReader.getParent( 1 );
            result.setSource( accumulateDescr );

        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( accumulateDescr );
        }

        return accumulateDescr;
    }

    public Class generateNodeFor() {
        return AccumulateDescr.class;
    }

}
