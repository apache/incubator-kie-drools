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

import org.drools.rule.Declaration;
import org.drools.rule.Query;
import org.drools.rule.Variable;

public final class DroolsQuery extends ArrayElements {
    private final String         name;
    private InternalViewChangedEventListener resultsCollector;
    private Query                query;
    private boolean              open;
    
    private Variable[]           vars;

    public DroolsQuery(final String name,
                       InternalViewChangedEventListener resultsCollector) {
        this( name,
              null,
              resultsCollector,
              false );
    }

    public DroolsQuery(final String name,
                       final Object[] params,
                       final InternalViewChangedEventListener resultsCollector,
                       final boolean open ) {
        super( params );
        this.name = name;
        this.resultsCollector = resultsCollector;
        this.open = open;
        
        // build the indexes to the Variables  
        if ( params != null ) {
            vars = new Variable[params.length];
            for ( int i = 0; i < params.length; i++ ) {
                // now record the var index positions and replace with null
                if ( params[i] == Variable.variable ) {
                    vars[i] = new Variable( getElements(),
                                           i );
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DroolsQuery other = (DroolsQuery) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }
    
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append( "query: " + name + "\n" + "parameters:\n" );
        
        if (this.query != null) {
            for ( Declaration declr : this.query.getParameters() ) {
                sbuilder.append( "   " );
                sbuilder.append( declr.getExtractor().getExtractToClass() + ":" + declr.getIdentifier() );
                sbuilder.append( "\n" );
            }
        }
        
        sbuilder.append( "collector: " + this.resultsCollector );
        
        return sbuilder.toString();
    }

}
