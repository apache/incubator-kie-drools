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

import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FieldConstraintDescr;
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
 *
 */
public class FromHandler extends BaseAbstractHandler
    implements
    Handler {

    public FromHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PatternDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FieldConstraintDescr.class );
            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {

        xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final FromDescr fromDesctiptor = new FromDescr();
        return fromDesctiptor;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {

        final Configuration config = xmlPackageReader.endConfiguration();

        final FromDescr fromDescr = (FromDescr) xmlPackageReader.getCurrent();

        Object parent = xmlPackageReader.getParent();

        final PatternDescr patternDescr = (PatternDescr) parent;

        final ConditionalElementDescr parentDescr = (ConditionalElementDescr)  xmlPackageReader.getParent( 1 );

        if ( (config.getChild( "expression" ) != null) ) {
            patternDescr.setSource( fromDescr );    
        }

        return fromDescr;
    }

    public Class generateNodeFor() {
        return FromDescr.class;
    }

}
