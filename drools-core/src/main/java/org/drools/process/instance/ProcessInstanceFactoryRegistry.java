/**
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

package org.drools.process.instance;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Process;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstanceFactory;

public class ProcessInstanceFactoryRegistry {
    
    public static final ProcessInstanceFactoryRegistry instance =
        new ProcessInstanceFactoryRegistry();

    private Map<Class< ? extends Process>, ProcessInstanceFactory> registry;

    public ProcessInstanceFactoryRegistry() {
        this.registry = new HashMap<Class< ? extends Process>, ProcessInstanceFactory>();

        // hard wired nodes:
        register( RuleFlowProcess.class,
                  new RuleFlowProcessInstanceFactory() );
    }

    public void register(Class< ? extends Process> cls,
                         ProcessInstanceFactory factory) {
        this.registry.put( cls,
                           factory );
    }

    public ProcessInstanceFactory getProcessInstanceFactory(Process process) {
        return this.registry.get( process.getClass() );
    }
}
