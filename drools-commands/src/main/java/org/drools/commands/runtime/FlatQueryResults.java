/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbListAdapter;
import org.drools.commands.jaxb.JaxbListWrapper;
import org.drools.commands.jaxb.JaxbStringObjectPair;
import org.drools.core.QueryResultsImpl;
import org.drools.core.QueryResultsRowImpl;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.rule.Declaration;
import org.drools.core.runtime.rule.impl.FlatQueryResultRow;
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

        identifiers  = new HashSet<>();
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
        idFactHandleMaps = new ArrayList<>();
        idResultMaps = new ArrayList<>();

        for ( QueryResultsRow result : results ) {
            QueryResultsRowImpl resultImpl = (QueryResultsRowImpl) result;
            Map<String, FactHandle> idFactHandleMap = new HashMap<>(length);
            Map<String, Object> idResultMap = new HashMap<>(length);

            for( i = 0; i < length; ++i ) {
                String id = declrs[i].getIdentifier();
                FactHandle factHandle = resultImpl.getFactHandle(id);
                if (factHandle != null) {
                    Object obj = null;
                    if ( !id.equals( "" ) ) {
                        // no result value "" because "abducibl/retrieved facts are hidden
                        obj = resultImpl.get( id );
                    }
                    factHandle = newFrom( factHandle );

                    idFactHandleMap.put( id, factHandle );
                    idResultMap.put( id, obj );
                }
            }
            idFactHandleMaps.add(idFactHandleMap);
            idResultMaps.add(idResultMap);
        }
    }

    // This does not put the object into the fact handle to avoid having it serialized when using JSON or JaxB, therefore
    // Reducing the payload by roughly a half (in case of many objects returned by the query)
    public static DisconnectedFactHandle newFrom( FactHandle handle ) {
        if( handle instanceof DisconnectedFactHandle ) {
            return (DisconnectedFactHandle) handle;
        } else {
            InternalFactHandle ifh = (InternalFactHandle) handle;
            return new DisconnectedFactHandle(ifh.getId(),
                                              ifh.getIdentityHashCode(),
                                              ifh.getObjectHashCode(),
                                              ifh.getRecency(),
                                              ifh.getEntryPointName(),
                                              null,
                                              ifh.isTraitOrTraitable() );
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
