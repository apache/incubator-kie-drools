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

import org.drools.commands.IdentifiableResult;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectsInEntryPointCommand
    implements
    ExecutableCommand<Collection>, IdentifiableResult {

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    private ObjectFilter filter = null;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    public GetObjectsInEntryPointCommand() {
    }

    public GetObjectsInEntryPointCommand(ObjectFilter filter, String entryPoint) {
        this.filter = filter;
        this.entryPoint = entryPoint;
    }

    public GetObjectsInEntryPointCommand(ObjectFilter filter, String entryPoint, String outIdentifier) {
        this.filter = filter;
        this.entryPoint = entryPoint;
        this.outIdentifier = outIdentifier;
    }

    public Collection execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Collection col;

        if ( filter != null ) {

            col =  ep.getObjects( this.filter );
        } else {
            col =  ep.getObjects( );
        }

        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );

            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, objects );
        }

        return col;
    }

    public String toString() {
        if ( filter != null ) {
            return "session.getEntryPoint( " + entryPoint + " ).iterateObjects( " + filter + " );";
        } else {
            return "session.getEntryPoint( " + entryPoint + " ).iterateObjects();";
        }
    }

}
