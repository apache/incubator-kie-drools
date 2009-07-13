package org.drools.process.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.impl.NativeQueryResults;

public class QueryCommand  implements GenericCommand<QueryResults> {
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

    public QueryResults execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        QueryResults results = null;
        
        if ( arguments == null || arguments.length == 0 ) {
            results = ksession.getQueryResults( name );
        } else {
            results = ksession.getQueryResults( name, this.arguments );
        }
        
        if ( this.outIdentifier != null ) {
            ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getResults().put( this.outIdentifier, results );
        }

        return results;
    }
}
