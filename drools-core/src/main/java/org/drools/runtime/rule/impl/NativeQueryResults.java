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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.rule.Declaration;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.spi.ObjectType;

public class NativeQueryResults
    implements
    QueryResults {
    
    private org.drools.QueryResults results;
    
    private String[] identifiers;
    
    public NativeQueryResults() {
    }

    public NativeQueryResults(org.drools.QueryResults results) {
        this.results = results;
    }

    public org.drools.QueryResults getResults() {
        return results;
    }

    public String[] getIdentifiers() {
        if ( identifiers != null ) {
            return identifiers;
        }
        Declaration[] parameters = this.results.getParameters();
        
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
        
        String[] declrs = new String[parameters.length + declrCollection.size() ];
        int i = 0;
        for ( Declaration declr : parameters ) {
            declrs[i++] = declr.getIdentifier();
        }
        for ( Declaration declr : declrCollection ) {
            declrs[i++] = declr.getIdentifier();
        }        
        return declrs;
    }
    
    
    public Map<String, Declaration> getDeclarations() {
        return this.getResults().getDeclarations(0);
    }

    public int size() {
        return this.getResults().size();
    }
    
    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( this.getResults().iterator() );
    }

    private class QueryResultsIterator
        implements
        Iterator<QueryResultsRow> {
        private Iterator<org.drools.QueryResult> iterator;

        public QueryResultsIterator(final Iterator<org.drools.QueryResult> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new NativeQueryResultRow(this.iterator.next());
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
