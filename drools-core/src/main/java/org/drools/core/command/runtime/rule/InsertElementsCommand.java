/*
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

package org.drools.core.command.runtime.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.util.StringUtils;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.xml.jaxb.util.JaxbCollectionAdapter;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.EntryPoint;

@XmlAccessorType( XmlAccessType.NONE )
public class InsertElementsCommand
    implements
    GenericCommand<Collection<FactHandle>>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlJavaTypeAdapter(JaxbCollectionAdapter.class)
    @XmlElement(name="list")
    public Collection<Object> objects;

    @XmlAttribute
    private String  outIdentifier;

    @XmlAttribute(name="return-objects")
    private boolean returnObject = true;
    
    @XmlAttribute(name="entry-point")
    private String entryPoint = "DEFAULT";

    public InsertElementsCommand() {
        this.objects = new ArrayList<Object>();
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
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        List<FactHandle> handles = new ArrayList<FactHandle>();
        
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
                ((StatefulKnowledgeSessionImpl)ksession).getExecutionResult().getResults().put( this.outIdentifier,
                                                               objects);
            }
            ((StatefulKnowledgeSessionImpl)ksession).getExecutionResult().getFactHandles().put( this.outIdentifier,
                                                               handles );
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
        List<Object> list = new ArrayList<Object>();
        for ( Object object : objects ) {
            list.add( object );
        }
        return "insert " + list;
    }

}
