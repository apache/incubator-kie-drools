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

package org.jbpm.process.audit;

import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.RuleBase;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;

/**
 * This class tests the following classes: 
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * </ul>
 */
public class WorkingMemoryDbLoggerWithSeparateLoggingEmfTest extends AbstractWorkingMemoryDbLoggerTest {

    private KieSession ksession = null;
   
    private EntityManagerFactory emf;
    
    @Before
    public void beforeThis() { 
        System.out.println("child");
        emf = Persistence.createEntityManagerFactory("org.jbpm.logging.jta");
        logService = new JPAAuditLogService(emf);
    }
   
    @After
    public void afterThis() { 
       if( emf != null && emf.isOpen() ) { 
           emf.close();
       }
       emf = null;
    }
    
    @Override
    public ProcessInstance startProcess(String processName) {
        if( ksession == null ) { 
            RuleBase ruleBase = createKnowledgeBase();
            KieBase kbase = new KnowledgeBaseImpl(ruleBase);
            
            Properties properties = new Properties();
            properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
            properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");

            KieSessionConfiguration conf = (KieSessionConfiguration) new SessionConfiguration(properties);
            Environment env = createEnvironment(context);
            ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);
            
            ksession.addEventListener(new JPAWorkingMemoryDbLogger(emf));
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
            

        }
        return ksession.startProcess(processName);
    }
    
}
