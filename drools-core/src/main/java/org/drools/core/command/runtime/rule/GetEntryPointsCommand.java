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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;

import org.drools.core.command.EntryPointCreator;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
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
        Collection<EntryPoint> result = new ArrayList<EntryPoint>();
        for (EntryPoint ep : eps) {
            result.add(epCreator.getEntryPoint(ep.getEntryPointId()));
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier,
                                                                                      result);
        }

        return result;
    }

    public String toString() {
        return "session.getEntryPoints( );";
    }
}
