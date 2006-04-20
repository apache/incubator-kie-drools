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

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class QueryHandler extends BaseAbstractHandler
    implements
    Handler {
    QueryHandler(XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PackageDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FunctionDescr.class );
            this.validPeers.add( RuleDescr.class );
            this.validPeers.add( QueryDescr.class );

            this.allowNesting = false;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                             attrs );

        String queryName = attrs.getValue( "name" );

        if ( queryName == null || queryName.trim().equals( "" ) ) {
            throw new SAXParseException( "<query> requires a 'name' attribute",
                                         xmlPackageReader.getLocator() );
        }

        QueryDescr queryDescr = new QueryDescr( queryName.trim() );

        return queryDescr;
    }

    public Object end(String uri,
                      String localName) throws SAXException {
        Configuration config = this.xmlPackageReader.endConfiguration();

        QueryDescr queryDescr = (QueryDescr) this.xmlPackageReader.getCurrent();

        AndDescr lhs = (AndDescr) queryDescr.getLhs();

        if ( lhs == null || lhs.getDescrs().isEmpty() ) {
            throw new SAXParseException( "<query> requires a LHS",
                                         xmlPackageReader.getLocator() );
        }

        this.xmlPackageReader.getPackageDescr().addRule( queryDescr );

        return null;
    }

    public Class generateNodeFor() {
        return QueryDescr.class;
    }
}