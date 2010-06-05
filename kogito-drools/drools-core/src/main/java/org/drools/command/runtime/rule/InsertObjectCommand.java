package org.drools.command.runtime.rule;

import java.io.ObjectStreamException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class InsertObjectCommand
    implements
    GenericCommand<FactHandle> {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private Object  object;

	@XmlAttribute(name="out-identifier", required=true)
    private String  outIdentifier;

    private boolean returnObject = true;
    
    public InsertObjectCommand() {
        
    }
    
    public InsertObjectCommand(Object object) {
        this.object = object;
    }

    public InsertObjectCommand(Object object, String outIdentifier) {
		super();
		this.object = object;
		this.outIdentifier = outIdentifier;
	}

	public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        FactHandle factHandle = ksession.insert( object );
        
        ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               object );
            }
            session.getExecutionResult().getFactHandles().put( this.outIdentifier,
                                                         factHandle );
        }

        return factHandle;
    }
    
    

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public boolean isReturnObject() {
        return returnObject;
    }

    public void setReturnObject(boolean returnObject) {
        this.returnObject = returnObject;
    }

    public String toString() {
        return "session.insert(" + object + ");";
    }
    
//    private Object readResolve() throws ObjectStreamException {
//        this.returnObject = true;
//        return this;
//    }

}
