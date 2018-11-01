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

import javax.xml.bind.annotation.XmlAttribute;

import org.drools.core.command.EntryPointCreator;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

public class GetEntryPointCommand
    implements
    ExecutableCommand<EntryPoint> {

    private String name;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetEntryPointCommand() {
    }

    public GetEntryPointCommand(String name) {
        this.name = name;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public EntryPoint execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(name);
        if (ep == null) {
            return null;
        }

        final EntryPointCreator epCreator = (EntryPointCreator)context.get(EntryPointCreator.class.getName());
        final EntryPoint entryPoint = epCreator != null ? epCreator.getEntryPoint(name) : ep;

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier,
                                                                                      entryPoint);
        }

        return entryPoint;
    }

    public String toString() {
        return "session.getEntryPoint( " + name + " );";
    }
}
