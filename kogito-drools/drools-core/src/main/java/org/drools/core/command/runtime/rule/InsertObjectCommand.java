/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.rule;


import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@XmlRootElement(name="insert-object-command")
@XmlAccessorType(XmlAccessType.NONE)
public class InsertObjectCommand
    implements
    GenericCommand<FactHandle>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlElement
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object  object;

    @XmlAttribute(name="out-identifier")
    private String  outIdentifier;

    @XmlAttribute(name="return-object")
    private boolean returnObject = true;

    @XmlAttribute(name="entry-point")
    private String entryPoint = "DEFAULT";
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
        KieSession ksession = (KieSession) ((Map<String, Object>)context.get(ContextImpl.REGISTRY)).get(KieSession.class.getName()); //((KnowledgeCommandContext) context).getKieSession();
        
        FactHandle factHandle;
        if ( StringUtils.isEmpty( this.entryPoint ) ) {
            factHandle = ksession.insert( object );
        } else {
            factHandle = ksession.getEntryPoint( this.entryPoint ).insert( object );
        }

        InternalWorkingMemory session = ((InternalWorkingMemory)ksession);

        if ( disconnected ) {
            DefaultFactHandle disconnectedHandle = ((DefaultFactHandle)factHandle).clone();
            disconnectedHandle.disconnect();
            return disconnectedHandle;
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
    
    public boolean isDisconnected() {
        return disconnected;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
    	if (entryPoint == null) {
    		entryPoint = "DEFAULT";
    	}
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
