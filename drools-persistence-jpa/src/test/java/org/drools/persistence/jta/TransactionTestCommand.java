/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.jta;

import bitronix.tm.TransactionManagerServices;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.api.runtime.Context;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

import static org.drools.persistence.jta.JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER;
import static org.drools.persistence.jta.JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER_FACTORY;


/**
 * Please make sure you understand foreign keys 
 * and how they work with regards to JPA before continuing further: 
 * - http://download.oracle.com/javaee/5/tutorial/doc/bnbqa.html#bnbqj
 * - http://en.wikipedia.org/wiki/Foreign_key
 * 
 *
 */
public class TransactionTestCommand implements ExecutableCommand<Void> {

    private static final long serialVersionUID = -7640078670024414748L;
    
    private Object mainObject;
    private Object subObject;
    private EntityManager em;
    private EntityManagerFactory emf;
    
    public TransactionTestCommand(Object mainObject, Object subObject, HashMap<String, Object> env) { 
      this.mainObject = mainObject;
      this.subObject = subObject; 
      setPersistenceFields(env);
    }
    
    public TransactionTestCommand(Object mainObject, HashMap<String, Object> env) { 
      this.mainObject = mainObject;
      this.subObject = null;
      setPersistenceFields(env);
    }

    private void setPersistenceFields(HashMap<String, Object> env) { 
        this.em = (EntityManager) env.get(COMMAND_ENTITY_MANAGER);
        assert this.em != null : "Command Entity Manager is null";
        this.emf = (EntityManagerFactory) env.get(COMMAND_ENTITY_MANAGER_FACTORY);
    }
    
    private HashMap<String, Object> getPersistenceEnvironment() { 
        HashMap<String, Object> env = new HashMap<String, Object>();
        env.put(COMMAND_ENTITY_MANAGER, this.em);
        env.put(COMMAND_ENTITY_MANAGER_FACTORY, this.emf);
        return env;
    }
    
    public Void execute(Context context) {
        em.joinTransaction();
        em.persist(mainObject);

        if( subObject != null ) { 
            KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
  
            // THe following 3 lines are the important ones! (See below for an explanation)
            KnowledgeBase cleanKBase = KnowledgeBaseFactory.newKnowledgeBase();
            cleanKBase.addKnowledgePackages(((KnowledgeBase)ksession.getKieBase()).getKnowledgePackages());
            StatefulKnowledgeSession commandKSession = JPAKnowledgeService.newStatefulKnowledgeSession( cleanKBase, null, initializeEnvironment() );

            /**
             *  Here's what's going on: 
             *  If the PersistableRunner (SSCS) & JtaTransactionManager (JTM) were _not_ aware of transactions,
             *  -> then inserting the mainObject _before_ having inserted the subObject
             *     would cause the operation/persist to fail and the transaction to fail. 
             *     - This is because the mainObject contains a foreign key referring to the subObject.
             *     
             *  However, the SSCS & JTM check whether or not they're owners of the transaction
             *  when starting and when committing the transaction they use. 
             *  -> So that when we insert the mainObject here (via a _new_ CommandBasedStatefulKnowledgeSession), 
             *     it does _not_ mess with the transaction state and the operation succeeds.  
             */ 
            TransactionTestCommand transactionTestSubCommand 
                = new TransactionTestCommand(this.subObject, getPersistenceEnvironment()); commandKSession.execute(transactionTestSubCommand);
        }

        return null;
    }
 
    private Environment initializeEnvironment() {
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        env.set(EnvironmentName.TRANSACTION_MANAGER,
                TransactionManagerServices.getTransactionManager());
        
        return env;
    }
    
}
