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
class ExistsHandler extends BaseAbstractHandler
    implements
    Handler {
    ExistsHandler(XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );
            this.validParents.add( OrDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( AndDescr.class );
            this.validPeers.add( OrDescr.class );
            this.validPeers.add( NotDescr.class );
            this.validPeers.add( ExistsDescr.class );
            this.validPeers.add( EvalDescr.class );
            this.validPeers.add( ColumnDescr.class );

            this.allowNesting = true;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                             attrs );
        ExistsDescr existsDescr = new ExistsDescr();

        return existsDescr;
    }

    public Object end(String uri,
                      String localName) throws SAXException {
        Configuration config = xmlPackageReader.endConfiguration();

        ExistsDescr existsDescr = (ExistsDescr) this.xmlPackageReader.getCurrent();

        if ( (existsDescr.getDescrs().size() != 1) && (existsDescr.getDescrs().get( 0 ).getClass() != ColumnDescr.class) ) {
            throw new SAXParseException( "<exists> can only have a single <column...> as a child element",
                                         xmlPackageReader.getLocator() );
        }

        LinkedList parents = this.xmlPackageReader.getParents();
        ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        Object parent = it.previous();

        ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
        parentDescr.addDescr( existsDescr );

        return null;
    }

    public Class generateNodeFor() {
        return ExistsDescr.class;
    }
}