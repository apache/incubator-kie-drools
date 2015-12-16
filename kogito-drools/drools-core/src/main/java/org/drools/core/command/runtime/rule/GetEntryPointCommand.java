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

import org.drools.core.command.EntryPointCreator;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

public class GetEntryPointCommand
    implements
    GenericCommand<EntryPoint> {

    private String name;
    
    public GetEntryPointCommand() {
    }

    public GetEntryPointCommand(String name) {
        this.name = name;
    }

    public EntryPoint execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        EntryPoint ep = ksession.getEntryPoint(name);
        if (ep == null) {
            return null;
        }

        EntryPointCreator epCreator = (EntryPointCreator)context.get(EntryPointCreator.class.getName());
        return epCreator != null ? epCreator.getEntryPoint(name) : ep;
    }

    public String toString() {
        return "session.getEntryPoint( " + name + " );";
    }
}
