package org.drools.command.runtime.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.result.ExecutionResults;
import org.drools.result.InsertElementsResult;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XmlAccessorType( XmlAccessType.NONE )
public class InsertElementsCommand
    implements
    GenericCommand<Collection<FactHandle>> {

    @XmlElement(name="object", required = true)
    protected List<Object> objects;

    @XmlAttribute
    private String  outIdentifier;

    @XmlAttribute
    private Boolean returnObject = true;

    public InsertElementsCommand() {
        this.objects = new ArrayList<Object>();
    }

    public InsertElementsCommand(Iterable<Object> objects) {
    	this();
    	for( Object obj: objects ){
    		this.objects.add( obj );
    	}
    }

    public Iterable<Object> getObjects() {
        return this.objects;
    }

    public void setObjects(Iterable<Object> objects) {
        this.objects = new ArrayList<Object>();
    	for( Object obj: objects ){
    		this.objects.add( obj );
    	}
    }

    public Collection<FactHandle> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        List<FactHandle> handles = new ArrayList<FactHandle>();
        for ( Object object : objects ) {
            handles.add( ksession.insert( object ) );
        }

        if ( outIdentifier != null ) {
            ExecutionResults execRes = ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult();
            InsertElementsResult insRes = new InsertElementsResult( outIdentifier );
            if ( this.returnObject ) {
                insRes.setObjects( objects ); 
            }
            insRes.setHandles( handles );
            execRes.getResults().add( insRes );
        }
        return handles;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public boolean isReturnObject() {
        if (returnObject == null) {
            return true;
        } else {
            return returnObject;
        }
    }

    public void setReturnObject(Boolean returnObject) {
        this.returnObject = returnObject;
    }

    public String toString() {
        List<Object> list = new ArrayList<Object>();
        for ( Object object : objects ) {
            list.add( object );
        }
        return "insert " + list;
    }

}
