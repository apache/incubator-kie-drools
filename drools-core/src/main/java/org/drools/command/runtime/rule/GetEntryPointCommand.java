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

package org.drools.command.runtime.rule;

import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.kie.command.Context;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.SessionEntryPoint;

public class GetEntryPointCommand
    implements
    GenericCommand<SessionEntryPoint> {

    private String name;

    public GetEntryPointCommand(String name) {
        this.name = name;
    }

    public SessionEntryPoint execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getEntryPoint( name );
    }

    public String toString() {
        return "session.getEntryPoint( " + name + " );";
    }
}
