/**
 * Copyright 2010 JBoss Inc
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

package org.drools.command.runtime.rule;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DefaultFactHandle;
import org.drools.core.util.StringUtils;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.xml.jaxb.util.JaxbUnknownAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class InsertObjectCommand
    implements
    GenericCommand<FactHandle> {

	private static final long serialVersionUID = 510l;

	@XmlElement
	@XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
	private Object  object;

	@XmlAttribute(name="out-identifier")
    private String  outIdentifier;

	@XmlAttribute(name="return-object")
    private boolean returnObject = true;
	
	@XmlAttribute(name="entry-point")
    private String entryPoint;
      	@XmlAttribute(name="disconnected")
    private boolean disconnected = false;
	
    public InsertObjectCommand() {
        
    }
    
    public InsertObjectCommand(Object object) {
        this.object = object;
    }

    public InsertObjectCommand(Object object, boolean disconnected) {
        this.object = object;
        this.disconnected = disconnected;
    }

    public InsertObjectCommand(Object object, String outIdentifier) {
		super();
		this.object = object;
		this.outIdentifier = outIdentifier;
	}

	public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        FactHandle factHandle;
        if ( StringUtils.isEmpty( this.entryPoint ) ) { 
            factHandle = ksession.insert( object );
        } else {
            factHandle = ksession.getWorkingMemoryEntryPoint( this.entryPoint ).insert( object );
        }
        
        ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               object );
            }
            session.getExecutionResult().getFactHandles().put( this.outIdentifier,
                                                         factHandle );
        }
        if ( disconnected ){
            ((DefaultFactHandle)factHandle).disconnect();
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
    
    

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public String toString() {
        return "session.entryPoints(" + ((this.entryPoint == null ) ? "DEFAULT" : this.entryPoint) + ").insert(" + object + ");";
    }
    
//    private Object readResolve() throws ObjectStreamException {
//        this.returnObject = true;
//        return this;
//    }

}
