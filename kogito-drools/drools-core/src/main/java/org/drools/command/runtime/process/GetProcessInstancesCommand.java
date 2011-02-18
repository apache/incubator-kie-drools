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

package org.drools.command.runtime.process;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class GetProcessInstancesCommand
    implements
    GenericCommand<Collection<ProcessInstance>> {

    public Collection<ProcessInstance> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Collection<ProcessInstance> instances = ksession.getProcessInstances();
        Collection<ProcessInstance> result = new ArrayList<ProcessInstance>();

        for ( ProcessInstance instance : instances ) {
            result.add( instance );
        }

        return result;
    }

    public String toString() {
        return "session.getProcessInstances();";
    }

}
