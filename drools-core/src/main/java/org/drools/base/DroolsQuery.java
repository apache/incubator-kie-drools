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

package org.drools.base;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.drools.core.util.RightTupleList;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.Query;
import org.drools.runtime.rule.Variable;

public final class DroolsQuery extends ArrayElements {
    private final String                      name;
    private InternalViewChangedEventListener  resultsCollector;
    private Query                             query;
    private boolean                           open;
    
    private Variable[]                        vars;
    
    private RightTupleList                    rightTupleList;
    
//    public DroolsQuery(DroolsQuery droolsQuery) {
//        super( new Object[droolsQuery.getElements().length] );
//
//        this.name = droolsQuery.getName();
//        this.resultsCollector = droolsQuery.getQueryResultCollector();
//        this.open = droolsQuery.isOpen();
//        final Object[] params = getElements();
//        System.arraycopy( droolsQuery.getElements(), 0, params, 0, params.length );
//        originalDroolsQuery = droolsQuery;
//    }

    public DroolsQuery(final String name,
                       final Object[] params,
                       final InternalViewChangedEventListener resultsCollector,
                       final boolean open ) {
        setParameters( params );
        this.name = name;
        this.resultsCollector = resultsCollector;
        this.open = open;                
    }
    
    public void setParameters(final Object[] params) {
        setElements( params );
        // build the indexes to the Variables  
        if ( params != null ) {
            vars = new Variable[params.length];
            for ( int i = 0; i < params.length; i++ ) {
                if ( params[i] == Variable.v ) {
                    vars[i] = Variable.v;
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
    

    public void setQuery(Query query) {
        // this is set later, as we don't yet know which Query will match this DroolsQuery propagation
        this.query = query;     
    }

    public Query getQuery() {
        return this.query;
    }

    public InternalViewChangedEventListener getQueryResultCollector() {
        return this.resultsCollector;
    }

    public boolean isOpen() {
        return open;
    }

    public RightTupleList getRightTupleList() {
        return rightTupleList;
    }

    public void setRightTupleList(RightTupleList rightTupleList) {
        this.rightTupleList = rightTupleList;
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
