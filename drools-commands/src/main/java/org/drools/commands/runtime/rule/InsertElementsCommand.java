/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.runtime.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.runtime.ExecutionResultImpl;
import org.drools.commands.jaxb.JaxbListAdapter;
import org.drools.util.StringUtils;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType( XmlAccessType.NONE )
public class InsertElementsCommand
    implements
    ExecutableCommand<Collection<FactHandle>>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlJavaTypeAdapter(JaxbListAdapter.class)
    @XmlElement(name="list")
    public Collection<Object> objects;

    @XmlAttribute
    private String  outIdentifier;

    @XmlAttribute(name="return-objects")
    private boolean returnObject = true;
    
    @XmlAttribute(name="entry-point")
    private String entryPoint = "DEFAULT";

    public InsertElementsCommand() {
        this.objects = new ArrayList<>();
    }

    public InsertElementsCommand(Collection<Object> objects) {
        this.objects = objects;
    }

    public InsertElementsCommand(String outIdentifier) {
        this();
        this.outIdentifier = outIdentifier;
    }

    public Collection<Object> getObjects() {
        return this.objects;
    }

    public void setObjects(Collection<Object> objects) {
        this.objects = objects;
    }

    public Collection<FactHandle> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        List<FactHandle> handles = new ArrayList<>();
        
        EntryPoint wmep;
        if ( StringUtils.isEmpty( this.entryPoint ) ) {
            wmep = ksession;
        } else {
            wmep = ksession.getEntryPoint( this.entryPoint );
        }

        for ( Object object : objects ) {
            handles.add( wmep.insert( object ) );
        }

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, objects );
            }
            ((ExecutionResultImpl) ((RegistryContext) context).lookup(ExecutionResults.class)).getFactHandles().put( this.outIdentifier, handles );
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
        return returnObject;
    }

    public void setReturnObject(boolean returnObject) {
        this.returnObject = returnObject;
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
        List<Object> list = new ArrayList<>();
        list.addAll(objects);
        return "insert " + list;
    }

}
