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

package org.drools.core.command.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;

@XmlRootElement(name="get-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetIdCommand
    implements
    ExecutableCommand<Long> {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetIdCommand() {
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Long execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        final Long identifier = ((StatefulKnowledgeSessionImpl)ksession).getIdentifier();

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier,
                                                                                      identifier);
        }

        return identifier;
    }

    public String toString() {
        return "session.getId( );";
    }
}
