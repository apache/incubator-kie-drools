package org.drools.command.runtime;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.result.ExecutionResults;
import org.drools.result.GetGlobalResult;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType( XmlAccessType.NONE )
public class GetGlobalCommand
    implements
    GenericCommand<Object> {

    @XmlAttribute(required = true)
    private String identifier;
    @XmlAttribute(name = "out-identifier")
    private String outIdentifier;

    public GetGlobalCommand() {
    }

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        Object object = ksession.getGlobal( identifier );

        // Simulation does not provide results.
        ExecutionResults execRes = (ExecutionResults)((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult();
        if( execRes != null ) {
        	String id = (this.outIdentifier != null) ? this.outIdentifier : this.identifier;
        	execRes.getResults().add( new GetGlobalResult( id, object ) );
        }

        return object;
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
