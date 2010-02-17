package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DisconnectedFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectCommand
    implements
    GenericCommand<Object> {

    private FactHandle factHandle;
    private String     outIdentifier;
    
    public GetObjectCommand() { }

    public GetObjectCommand(FactHandle factHandle) {
        this.factHandle = factHandle;
    }
    
    public GetObjectCommand(FactHandle factHandle, String outIdentifier) {
        this.factHandle = factHandle;
		this.outIdentifier = outIdentifier;
    }

    @XmlAttribute(name="out-identifier", required=true)
    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @XmlAttribute(name="factHandle", required=true)
    public void setFactHandleFromString(String factHandleId) {
    	factHandle = new DisconnectedFactHandle(factHandleId);
	}
    
    public String getFactHandleFromString() {
    	return factHandle.toExternalForm();
	}

	public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Object object = ksession.getObject( factHandle );
        
        if (this.outIdentifier != null) {
        	((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult()
        		.getResults().put( this.outIdentifier, object );
        }
        
        return object;
    }
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
