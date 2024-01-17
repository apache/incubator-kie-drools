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
import jakarta.xml.bind.annotation.XmlAttribute;

import org.drools.commands.EntryPointCreator;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.command.RegistryContext;

public class GetEntryPointsCommand
    implements
    ExecutableCommand<Collection< ? extends EntryPoint>> {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public GetEntryPointsCommand() {
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection< ? extends EntryPoint> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        Collection< ? extends EntryPoint> eps = ksession.getEntryPoints();
        EntryPointCreator epCreator = (EntryPointCreator)context.get(EntryPointCreator.class.getName());
        if (epCreator == null) {
            return eps;
        }
        Collection<EntryPoint> result = new ArrayList<>();
        for (EntryPoint ep : eps) {
            result.add(epCreator.getEntryPoint(ep.getEntryPointId()));
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, result);
        }

        return result;
    }

    public String toString() {
        return "session.getEntryPoints( );";
    }
}
