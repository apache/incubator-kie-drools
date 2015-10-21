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

package org.drools.core.command.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SetGlobalCommand
    implements
    GenericCommand<Object> , IdentifiableResult{

    @XmlAttribute(required=true)
    private String  identifier;

    @XmlElement
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object  object;

    @XmlAttribute(name="out-identifier")
    private String  outIdentifier;

    public SetGlobalCommand() {
    }

    public SetGlobalCommand(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public Object execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();

        if ( this.outIdentifier != null ) {
            ((StatefulKnowledgeSessionImpl) ksession).getExecutionResult().getResults().put( this.outIdentifier ,
                                                                                                     object );
        }

        ksession.setGlobal( this.identifier,
                            this.object );

        // returning the object is necessary for drools-simulator
        return this.object;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public String toString() {
        return "session.setGlobal(" + this.identifier + ", " + this.object + ");";
    }

}
