/*
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.rule.Declaration;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.spi.ObjectType;
import org.drools.xml.jaxb.util.JaxbFlatQueryResultsAdapter;
import org.drools.xml.jaxb.util.JaxbMapAdapter;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType(name="query-results")
@XmlRootElement
public class FlatQueryResults
    implements
    QueryResults {
    
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="identifiers")    
    private Map<String, Integer> identifiers;
    
    @XmlElement(name="results")
    @XmlJavaTypeAdapter(JaxbFlatQueryResultsAdapter.class)
    private ArrayList<ArrayList<Object>>           results;
    
    
    @XmlJavaTypeAdapter(JaxbFlatQueryResultsAdapter.class)
    @XmlElement(name="fact-handles")
    private ArrayList<ArrayList<FactHandle>> factHandles;
    
    public FlatQueryResults() {

    }

    public FlatQueryResults(Map<String, Integer> identifiers,
                            ArrayList<ArrayList<Object>> results,
                            ArrayList<ArrayList<FactHandle>> factHandles) {
        this.identifiers = identifiers;
        this.results = results;
        this.factHandles = factHandles;
    }
    
    public FlatQueryResults(org.drools.QueryResults results) {        
        Declaration[] parameters = results.getParameters();
        
        Set<String> set  = new HashSet<String>();
        for ( Declaration declr : parameters ) {
            set.add( declr.getIdentifier() );
        }
        
        
        Collection<Declaration> declrCollection = new ArrayList( results.getDeclarations(0).values() );
        
        for ( Iterator<Declaration> it =  declrCollection.iterator(); it.hasNext(); ) {
            Declaration declr = it.next();
            if ( set.contains( declr.getIdentifier()  ) ) {
                it.remove();
            }
        }   
        
        Declaration[] declrs = new Declaration[parameters.length + declrCollection.size() ];
        int i = 0;
        for ( Declaration declr : parameters ) {
            declrs[i++] = declr;
        }
        for ( Declaration declr : declrCollection ) {
            declrs[i++] = declr;
        }       
        

        this.results = new ArrayList<ArrayList<Object>>( results.size() );
        this.factHandles = new ArrayList<ArrayList<FactHandle>> ( results.size() );

        int length = declrs.length;
        
        identifiers = new LinkedHashMap<String, Integer>( length );
        for ( i = 0; i < length; i++ ) {
            identifiers.put( declrs[i].getIdentifier(),
                             i );
        }
        
        
        for ( org.drools.QueryResult result : results ) {
            ArrayList<Object> row = new ArrayList<Object>();
            ArrayList<FactHandle> rowHandle = new ArrayList<FactHandle>();

            for ( i = 0; i < length; i++ ) {
                Declaration declr = declrs[i];
                row.add( result.get( declr ) );
                rowHandle.add( result.getFactHandle( declr ) );
            }

            this.results.add( row );
            this.factHandles.add( rowHandle );
        }



    }

    public String[] getIdentifiers() {
        return identifiers.keySet().toArray( new String[identifiers.size()] );
    }

    public int size() {
        return this.results.size();
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( identifiers,
                                         this.results.iterator(),
                                         this.factHandles.iterator() );
    }

    private class QueryResultsIterator implements Iterator<QueryResultsRow> {
        private Map<String, Integer> identifiers;
        private Iterator<ArrayList<Object>> iterator;
        private Iterator<ArrayList<FactHandle>> handleIterator;

        public QueryResultsIterator(Map<String, Integer> identifiers,
                                    final Iterator<ArrayList<Object>> iterator,
                                    final Iterator<ArrayList<FactHandle>> handleIterator) {
            this.identifiers = identifiers;
            this.iterator = iterator;
            this.handleIterator = handleIterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new FlatQueryResultRow( identifiers,
                                           this.iterator.next(),
                                           this.handleIterator.next() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
