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
package org.drools.core.base;

import java.util.Arrays;
import java.util.List;

import org.drools.base.base.DroolsQuery;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.core.common.TupleSets;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.util.index.TupleList;
import org.kie.api.runtime.rule.Variable;

public final class DroolsQueryImpl extends ArrayElements implements DroolsQuery{
    private final String                           name;
    private       InternalViewChangedEventListener resultsCollector;
    private QueryImpl query;
    private       boolean                          open;

    private Variable[] vars;

    private TupleList resultInsertRightTupleList;
    private TupleList resultUpdateRightTupleList;
    private TupleList resultRetractRightTupleList;

    private WorkingMemoryAction action;

    private final TupleSets resultLeftTuples;

    private QueryElementNodeMemory qmem;

    private List<PathMemory> pmems;

    private StackEntry stackEntry;

    private LeftTupleSink sink;

    public DroolsQueryImpl(String name, Object[] params, InternalViewChangedEventListener resultsCollector, boolean open) {
        this(name, params, resultsCollector, open, null, null, null, null, null);
    }

    public DroolsQueryImpl(final String name,
                           final Object[] params,
                           final InternalViewChangedEventListener resultsCollector,
                           final boolean open,
                           final StackEntry stackEntry,
                           final List<PathMemory> pmems,
                           final TupleSets resultLeftTuples,
                           final QueryElementNodeMemory qmem,
                           final LeftTupleSink sink) {
        setParameters(params);
        this.name = name;
        this.resultsCollector = resultsCollector;
        this.stackEntry = stackEntry;
        this.open = open;
        this.pmems = pmems;
        this.resultLeftTuples = resultLeftTuples;
        this.qmem = qmem;
        this.sink = sink;
    }
    
    public void setParameters(final Object[] params) {
        setElements( params );
        // build the indexes to the Variables  
        if ( params != null ) {
            vars = new Variable[params.length];
            for ( int i = 0; i < params.length; i++ ) {
                if ( params[i] == Variable.v ) {
                    vars[i] = Variable.v;
                    params[i] = null;
                }
            }
        }        
    }

    public String getName() {
        return this.name;
    }
    
    public Variable[] getVariables() {
        return this.vars;
    }  
    
    public TupleSets getResultLeftTupleSets() {
        return resultLeftTuples;
    }

    public StackEntry getStackEntry() {
        return this.stackEntry;
    }

    public List<PathMemory> getRuleMemories() {
        return this.pmems;
    }

    public QueryElementNodeMemory getQueryNodeMemory() {
        return qmem;
    }

    public LeftTupleSink getLeftTupleSink() {
        return this.sink;
    }

    public void setQuery(QueryImpl query) {
        // this is set later, as we don't yet know which Query will match this DroolsQuery propagation
        this.query = query;     
    }

    public QueryImpl getQuery() {
        return this.query;
    }

    public InternalViewChangedEventListener getQueryResultCollector() {
        return this.resultsCollector;
    }

    public boolean isOpen() {
        return open;
    }

    public TupleList getResultInsertRightTupleList() {
        return resultInsertRightTupleList;
    }

    public void setResultInsertRightTupleList(TupleList evaluateActionsRightTupleList) {
        this.resultInsertRightTupleList = evaluateActionsRightTupleList;
    }

    public TupleList getResultUpdateRightTupleList() {
        return resultUpdateRightTupleList;
    }

    public void setResultUpdateRightTupleList(TupleList insertUpdateRightTupleList) {
        this.resultUpdateRightTupleList = insertUpdateRightTupleList;
    }

    public TupleList getResultRetractRightTupleList() {
        return resultRetractRightTupleList;
    }

    public void setResultRetractRightTupleList(TupleList retractRightTupleList) {
        this.resultRetractRightTupleList = retractRightTupleList;
    } 

    public WorkingMemoryAction getAction() {
        return action;
    }

    public void setAction(WorkingMemoryAction action) {
        this.action = action;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (open ? 1231 : 1237);
        result = prime * result + Arrays.hashCode( vars );
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        // DroolsQuery must be instance equals
        return this == obj;
    }

    @Override
    public String toString() {
        return "DroolsQuery [name=" + name + ", resultsCollector=" + resultsCollector + 
                    ", query=" + query + ", open=" + open +
                    ", args=" + Arrays.toString( getElements() ) +
                    ", vars=" + Arrays.toString( vars ) + "]";
    }
}
