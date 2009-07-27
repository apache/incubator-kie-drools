package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;

import org.drools.result.ExecutionResults;
import org.drools.result.InsertObjectResult;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType( XmlAccessType.NONE )
public class InsertObjectCommand
    implements
    GenericCommand<FactHandle> {

    @XmlElement(required = true)
    private Object  object;

    @XmlAttribute(name = "out-identifier")
    private String  outIdentifier;

    @XmlAttribute(name = "return-object")
    private boolean returnObject = true;

    public InsertObjectCommand() {
    }

    public InsertObjectCommand(Object object) {
        this.object = object;
    }

    public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        FactHandle factHandle = ksession.insert( object );
        
        ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;
        if ( outIdentifier != null ) {
            ExecutionResults execRes = ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult();
            InsertObjectResult insRes = new InsertObjectResult( outIdentifier, factHandle );
            if ( this.returnObject ) {
                insRes.setObject( object ); 
            }
            execRes.getResults().add( insRes );
        }

        return factHandle;
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

}
