/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit;

import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.process.audit.AbstractAuditLogServiceTest.createKieSession;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This class tests the following classes: 
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * </ul>
 */
public class WorkingMemoryDbLoggerWithPersistentSessionTest extends AbstractWorkingMemoryDbLoggerTest {

    private KieSession ksession = null;
    
    @Override
    public ProcessInstance startProcess(String processName) {
        if( ksession == null ) { 
            KieBase kbase = createKnowledgeBase();
            
            Environment env = createEnvironment(context);
            ksession = createKieSession(kbase, env);
            
            new JPAWorkingMemoryDbLogger(ksession);
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        }
        return ksession.startProcess(processName);
    }
    
}
