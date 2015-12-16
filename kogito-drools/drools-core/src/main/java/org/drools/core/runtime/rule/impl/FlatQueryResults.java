/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.runtime.rule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.QueryResultsImpl;
import org.drools.core.QueryResultsRowImpl;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.xml.jaxb.util.JaxbListAdapter;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.drools.core.xml.jaxb.util.JaxbObjectObjectPair;
import org.drools.core.xml.jaxb.util.JaxbStringObjectPair;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

@XmlAccessorType( XmlAccessType.NONE )
@XmlType(name="query-results")
@XmlRootElement
@XmlSeeAlso(value={DisconnectedFactHandle.class, JaxbListWrapper.class, JaxbStringObjectPair.class})
public class FlatQueryResults implements QueryResults {

    @XmlElement
    @XmlJavaTypeAdapter(JaxbListAdapter.class)
    private ArrayList<Map<String, FactHandle>> idFactHandleMaps;

    @XmlElement
    @XmlJavaTypeAdapter(JaxbListAdapter.class)
    private ArrayList<Map<String, Object>> idResultMaps;

    @XmlElement
    @XmlJavaTypeAdapter(JaxbListAdapter.class)
    private Set<String> identifiers = null;

    public FlatQueryResults() {
        // JAXB constructor
    }

    public FlatQueryResults(Set<String> identifiers,
                            ArrayList<Map<String, FactHandle>> idFactHandleMaps,
                            ArrayList<Map<String, Object>> factHandleResultMaps) {
        this.identifiers = identifiers;
        this.idFactHandleMaps = idFactHandleMaps;
        this.idResultMaps = factHandleResultMaps;
    }

    public FlatQueryResults(QueryResultsImpl results) {
        Declaration[] parameters = results.getParameters();

        identifiers  = new HashSet<String>();
        for ( Declaration declr : parameters ) {
            identifiers.add( declr.getIdentifier() );
        }

        Collection<Declaration> declrCollection = new ArrayList( results.getDeclarations(0).values() );
        for ( Iterator<Declaration> it =  declrCollection.iterator(); it.hasNext(); ) {
            Declaration declr = it.next();
            if ( ! identifiers.add( declr.getIdentifier()  ) ) {
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

        int length = declrs.length;
        idFactHandleMaps = new ArrayList<Map<String,FactHandle>>();
        idResultMaps = new ArrayList<Map<String,Object>>();

        for ( QueryResultsRow result : results ) {
            QueryResultsRowImpl resultImpl = (QueryResultsRowImpl) result;
            Map<String, FactHandle> idFactHandleMap = new HashMap<String, FactHandle>(length);
            Map<String, Object> idResultMap = new HashMap<String, Object>(length);

            for( i = 0; i < length; ++i ) {
                String id = declrs[i].getIdentifier();
                FactHandle factHandle = resultImpl.getFactHandle(id);
                Object obj = null;
                if( ! id.equals("") ) {
                    // no result value "" because "abducibl/retrieved facts are hidden
                    obj = resultImpl.get(id);
                }
                factHandle = DisconnectedFactHandle.newFrom(factHandle);

                idFactHandleMap.put(id, factHandle);
                idResultMap.put(id, obj);
            }
            idFactHandleMaps.add(idFactHandleMap);
            idResultMaps.add(idResultMap);
        }
    }

    public String[] getIdentifiers() {
        if( identifiers == null ) {
            return new String[0];
        }
        return identifiers.toArray(new String[identifiers.size()]);
    }

    public int size() {
        return this.idFactHandleMaps.size();
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( idFactHandleMaps.iterator(),
                                         idResultMaps.iterator() );
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof FlatQueryResults) ) return false;

        FlatQueryResults that = (FlatQueryResults) o;

        if ( idFactHandleMaps != null ? !idFactHandleMaps.equals( that.idFactHandleMaps ) : that.idFactHandleMaps != null ) return false;
        if ( idResultMaps != null ? !idResultMaps.equals( that.idResultMaps ) : that.idResultMaps != null ) return false;
        return !(identifiers != null ? !identifiers.equals( that.identifiers ) : that.identifiers != null);

    }

    @Override
    public int hashCode() {
        int result = idFactHandleMaps != null ? idFactHandleMaps.hashCode() : 0;
        result = 31 * result + (idResultMaps != null ? idResultMaps.hashCode() : 0);
        result = 31 * result + (identifiers != null ? identifiers.hashCode() : 0);
        return result;
    }

    private static class QueryResultsIterator implements Iterator<QueryResultsRow> {
        private Iterator<Map<String, FactHandle>> handleIterator;
        private Iterator<Map<String, Object>> iterator;

        public QueryResultsIterator(final Iterator<Map<String, FactHandle>> handleIterator,
                                    final Iterator<Map<String, Object>> iterator) {
            this.handleIterator = handleIterator;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new FlatQueryResultRow( handleIterator.next(),
                                           iterator.next() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
