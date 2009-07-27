package org.drools.command.runtime;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.drools.KnowledgeBase;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.result.ExecutionResults;
import org.drools.result.SetGlobalResult;
import org.drools.runtime.StatefulKnowledgeSession;

@XmlAccessorType( XmlAccessType.NONE )
public class SetGlobalCommand
    implements
    GenericCommand<Void> {

    @XmlAttribute(required = true)
    private String identifier;

    @XmlElement
    private Object object;
    
    @XmlAttribute(name = "out-identifier")
    private String outIdentifier;
    
    @XmlAttribute
    private Boolean out;

    public SetGlobalCommand(){
    }

    public SetGlobalCommand(String identifier,
                            Object object) {
    	this.identifier = identifier;
    	this.object = object;
    }

    public Void execute(Context context) {
    	StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

    	ksession.setGlobal( this.identifier, this.object );

    	if ( this.isOut() ) {
    		ExecutionResults execRes = (ExecutionResults)((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult();
    		String id =  (this.outIdentifier != null) ? this.outIdentifier : this.identifier;
    		execRes.getResults().add( new SetGlobalResult( id, object ) );
    	}
    	return null;
    }

    public Object getObject() {
    	return this.object;
    }

    public void setObject( Object object ) {
    	this.object = object;
    }

    public String getOutIdentifier() {
    	return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
    	this.outIdentifier = outIdentifier;
    	this.out = true;
    }

    public String getIdentifier() {
    	return this.identifier;
    }

    public void setIdentifier(String identifier) {
    	this.identifier = identifier;
    }

    public boolean isOut() {
    	if (out == null) {
    		return false;
    	} else {
    		return out;
    	}
    }

    public void setOut(Boolean out) {
    	this.out = out;
    }

    public String toString() {
    	return "session.setGlobal(" + this.identifier + ", " + this.object + ");";
    }

}
