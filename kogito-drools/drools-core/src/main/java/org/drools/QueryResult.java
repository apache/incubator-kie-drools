package org.drools;
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

import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class QueryResult {

    protected Tuple       tuple;
    private WorkingMemory workingMemory;
    private QueryResults  queryResults;

    public QueryResult(final Tuple tuple,
                       final WorkingMemory workingMemory,
                       final QueryResults queryResults) {
        this.tuple = tuple;
        this.workingMemory = workingMemory;
        this.queryResults = queryResults;
    }

    /**
     * Return a map of Declarations where the key is the identifier and the value
     * is the Declaration.
     * 
     * @return
     *      The Map of Declarations.
     */    
    public Map getDeclarations() {
        return this.queryResults.getDeclarations();
    }

    /**
     * Returns the Object for int position in the Tuple
     * 
     * @param i
     * @return
     *     The Object
     */
    public Object get(final int i) {
        //adjust for the DroolsQuery object
        return getObject( this.tuple.get( i + 1 ));
    }

    /** 
     * Return the Object for the given Declaration identifer.
     * @param identifier
     * @return
     *      The Object
     */
    public Object get(final String identifier) {
        return get( (Declaration) this.queryResults.getDeclarations().get( identifier ) );
    }

    /** 
     * Return the Object for the given Declaration.
     * @param identifier
     * @return
     *      The Object
     */    
    public Object get(final Declaration declaration) {
        return declaration.getValue( (InternalWorkingMemory) workingMemory, getObject( this.tuple.get( declaration ) ) );
    }
    
    public FactHandle getFactHandle(String identifier) {
        return this.tuple.get( ( Declaration ) this.queryResults.getDeclarations().get( identifier ) );
    }
    
    public FactHandle getFactHandle(Declaration declr) {
        return this.tuple.get( declr );
    }    

    /**
     * Return the FactHandles for the Tuple.
     * @return
     */
    public FactHandle[] getFactHandles() {
        // Strip the DroolsQuery fact
        final FactHandle[] src = this.tuple.getFactHandles();
        final FactHandle[] dst = new FactHandle[src.length - 1];
        System.arraycopy( src,
                          1,
                          dst,
                          0,
                          dst.length );
        return dst;
    }

    /**
     * The size of the Tuple; i.e. the number of columns (FactHandles) in this row result.
     * @return
     */
    public int size() {
        // Adjust for the DroolsQuery object
        return this.tuple.getFactHandles().length - 1;
    }
    
    /**
     * Get the Object for the given FactHandle
     * @param handle
     * @return
     */
    private Object getObject(FactHandle factHandle) {
        return (( InternalFactHandle ) factHandle).getObject(); 
    }
}
