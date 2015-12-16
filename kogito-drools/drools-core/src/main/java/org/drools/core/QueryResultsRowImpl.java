/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core;

import java.util.Map;

import org.drools.core.base.QueryRowWithSubruleIndex;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResultsRow;

public class QueryResultsRowImpl implements QueryResultsRow {

    protected QueryRowWithSubruleIndex row;
    private WorkingMemory workingMemory;
    private QueryResultsImpl queryResults;

    public QueryResultsRowImpl(final QueryRowWithSubruleIndex row,
                               final WorkingMemory workingMemory,
                               final QueryResultsImpl queryResults) {
        this.row = row;
        this.workingMemory = workingMemory;
        this.queryResults = queryResults;
    }

    public int getSubruleIndex() {
        return this.row.getSubruleIndex();
    }

    /**
     * Return a map of Declarations where the key is the identifier and the value
     * is the Declaration.
     *
     * @return
     *      The Map of Declarations.
     */
    public Map<String, Declaration> getDeclarations() {
        return this.queryResults.getDeclarations(row.getSubruleIndex());
    }

    /**
     * Returns the Object for int position in the Tuple
     *
     * @param i
     * @return
     *     The Object
     */
    public Object get(final int i) {
        return getObject( this.row.getHandles()[ i + 1]); // Add one, as we hide root DroolsQuery
    }


    /*
     * (non-Javadoc)
     * @see org.kie.api.runtime.rule.QueryResultsRow#get(java.lang.String)
     */
    @Override
    public Object get(final String identifier) {
        Declaration decl = getDeclarations().get( identifier );
        if ( decl == null ) {
            throw new IllegalArgumentException( "identifier '" + identifier + "' cannot be found" );
        }
        return get( decl );
    }

    /**
     * Return the Object for the given Declaration.
     */
    public Object get(final Declaration declaration) {
        return declaration.getValue( (InternalWorkingMemory) workingMemory, getObject( getFactHandle( declaration ) ) );
    }

    /*
     * (non-Javadoc)
     * @see org.kie.api.runtime.rule.QueryResultsRow#getFactHandle(java.lang.String)
     */
    @Override
    public FactHandle getFactHandle(String identifier) {
        return getFactHandle( getDeclarations().get( identifier ) );
    }

    public FactHandle getFactHandle(Declaration declr) {
        return this.row.getHandles()[  declr.getPattern().getOffset() ];
    }

    public FactHandle getFactHandle(int i) {
        return this.row.getHandles()[ i + 1 ];
    }

    /**
     * Return the FactHandles for the Tuple.
     * @return
     */
    public FactHandle[] getFactHandles() {
        int size = size();
        FactHandle[] subArray = new FactHandle[ size];

        System.arraycopy( this.row.getHandles(), 1, subArray, 0, size );
        return subArray;
    }

    /**
     * The size of the Tuple; i.e. the number of columns (FactHandles) in this row result.
     * @return
     */
    public int size() {
        return this.row.getHandles().length -1;
    }

    /**
     * Get the Object for the given FactHandle
     */
    private Object getObject(FactHandle factHandle) {
        return (( InternalFactHandle ) factHandle).getObject();
    }
}
