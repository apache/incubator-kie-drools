package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.impl.NativeQueryResults;

public class QueryCommand  implements Command<QueryResults> {
    private String outIdentifier;
    private String name;
    private Object[] arguments;
    
    public QueryCommand(String outIdentifier, String name, Object[] arguments) {
        this.outIdentifier = outIdentifier;
        this.name = name;
        this.arguments = arguments;
    }
    
    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Object[] getArguments() {
        return arguments;
    }
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public QueryResults execute(ReteooWorkingMemory session) {
        QueryResults results = null;
        
        if ( arguments == null || arguments.length == 0 ) {
            results = new NativeQueryResults( session.getQueryResults( name ) );
        } else {
            results = new NativeQueryResults( session.getQueryResults( name, this.arguments ) );
        }
        
        if ( this.outIdentifier != null ) {
            session.getExecutionResult().getResults().put( this.outIdentifier, results );
        }

        return results;
    }
}
