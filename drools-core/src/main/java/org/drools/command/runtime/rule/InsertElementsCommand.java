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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.xml.jaxb.util.JaxbListAdapter;
import org.drools.xml.jaxb.util.JaxbListWrapper;

@XmlAccessorType( XmlAccessType.NONE )
public class InsertElementsCommand
    implements
    GenericCommand<Collection<FactHandle>> {

	private static final long serialVersionUID = 510l;

	@XmlJavaTypeAdapter(JaxbListAdapter.class)
	@XmlElement(name="list")
	public List<Object> objects;

    @XmlAttribute
    private String  outIdentifier;

    @XmlAttribute(name="return-objects")
    private boolean returnObject = true;

    public InsertElementsCommand() {
        this.objects = new ArrayList<Object>();
    }

    public InsertElementsCommand(List<Object> objects) {
        this.objects = objects;
    }

    public InsertElementsCommand(String outIdentifier) {
		this();
		this.outIdentifier = outIdentifier;
	}

	public List<Object> getObjects() {
        return this.objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public Collection<FactHandle> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for ( Object object : objects ) {
            handles.add( ksession.insert( object ) );
        }

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               objects);
            }
            ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getFactHandles().put( this.outIdentifier,
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

    public String toString() {
        List<Object> list = new ArrayList<Object>();
        for ( Object object : objects ) {
            list.add( object );
        }
        return "insert " + list;
    }

}
