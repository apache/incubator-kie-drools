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

import static org.drools.persistence.jta.JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER;
import static org.drools.persistence.jta.JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER_FACTORY;

import java.io.Serializable;
import java.util.HashMap;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.infinispan.marshaller.InfinispanPlaceholderResolverStrategy;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.command.Context;
import org.kie.internal.persistence.infinispan.InfinispanKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import bitronix.tm.TransactionManagerServices;


/**
 * Please make sure you understand foreign keys 
 * and how they work with regards to JPA before continuing further: 
 * - http://download.oracle.com/javaee/5/tutorial/doc/bnbqa.html#bnbqj
 * - http://en.wikipedia.org/wiki/Foreign_key
 * 
 *
 */
public class TransactionTestCommand implements GenericCommand<Void> {

    private static final long serialVersionUID = -7640078670024414748L;
    
    private Object mainObject;
    private Object subObject;
    private Cache<Serializable, Object> cache;
    private DefaultCacheManager cm;
    
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

    @SuppressWarnings("unchecked")
    private void setPersistenceFields(HashMap<String, Object> env) { 
        this.cache = (Cache<Serializable, Object>) env.get(COMMAND_ENTITY_MANAGER);
        this.cm = (DefaultCacheManager) env.get(COMMAND_ENTITY_MANAGER_FACTORY);
    }
    
    private HashMap<String, Object> getPersistenceEnvironment() { 
        HashMap<String, Object> env = new HashMap<String, Object>();
        env.put(COMMAND_ENTITY_MANAGER, this.cache);
        env.put(COMMAND_ENTITY_MANAGER_FACTORY, this.cm);
        return env;
    }
    
    public Void execute(Context context) {
        //cache.joinTransaction();TODO
    	TransactionTestObject obj = (TransactionTestObject) mainObject;
    	if (obj.getId() == null) {
    		obj.setId(generateId());
    	}
        cache.put(InfinispanPlaceholderResolverStrategy.getClassIdValue(mainObject), mainObject);

        if( subObject != null ) { 
            KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
  
            // THe following 3 lines are the important ones! (See below for an explanation)
            KnowledgeBase cleanKBase = KnowledgeBaseFactory.newKnowledgeBase();
            cleanKBase.addKnowledgePackages(((KnowledgeBase)ksession.getKieBase()).getKnowledgePackages());
            StatefulKnowledgeSession commandKSession = InfinispanKnowledgeService.newStatefulKnowledgeSession( cleanKBase, null, initializeEnvironment() );

            /**
             *  Here's what's going on: 
             *  If the SingleSessionCommandService (SSCS) & JtaTransactionManager (JTM) were _not_ aware of transactions, 
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
 
    private static long value = 1;
    
    private static Long generateId() {
    	return ++value;
    }
    
    private Environment initializeEnvironment() {
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, cm);
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        env.set(EnvironmentName.TRANSACTION_MANAGER,
                TransactionManagerServices.getTransactionManager());
        
        return env;
    }
    
}
