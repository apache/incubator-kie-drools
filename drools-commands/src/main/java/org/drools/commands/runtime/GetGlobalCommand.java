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
package org.drools.commands.runtime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.drools.commands.IdentifiableResult;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetGlobalCommand
    implements
    ExecutableCommand<Object>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute (required=true)
    private String identifier;
    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetGlobalCommand() {
    }

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );

        Object object = ksession.getGlobal( identifier );
        ExecutionResults results = ((RegistryContext)context).lookup( ExecutionResults.class );
        if ( results != null ) {
            results.getResults().put( (this.outIdentifier != null) ? this.outIdentifier : this.identifier, object );
        }
        return object;
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
