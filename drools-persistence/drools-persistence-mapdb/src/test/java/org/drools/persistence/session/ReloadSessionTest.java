/*
 * Copyright 2011 Red Hat Inc
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
package org.drools.persistence.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.drools.core.common.DefaultFactHandle;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.mapdb.MapDBEnvironmentName;
import org.drools.persistence.mapdb.MapDBUserTransaction;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;
import org.mapdb.DB;

public class ReloadSessionTest {

    // Datasource (setup & clean up)
    private Map<String, Object> context;
    private DB db;

    private static String simpleRule = "package org.kie.test\n"
            + "global java.util.List list\n" 
            + "rule rule1\n" 
            + "when\n"
            + "  Integer(intValue > 0)\n" 
            + "then\n" 
            + "  list.add( 1 );\n"
            + "end\n" 
            + "\n";


    @Before
    public void setup() {
        context = MapDBPersistenceUtil.setupMapDB();
        db = (DB) context.get(MapDBEnvironmentName.DB_OBJECT);

    }

    @After
    public void cleanUp() {
        MapDBPersistenceUtil.cleanUp(context);
    }

    private Environment createEnvironment() { 
        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        return env;
    }
    private KieBase initializeKnowledgeBase(String rule) { 
        // Initialize knowledge base/session/etc..
    	KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(rule.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        return kbase;
    }
    
    @Test 
    public void reloadKnowledgeSessionTest() { 
        
        // Initialize drools environment stuff
        Environment env = createEnvironment();
        KieBase kbase = initializeKnowledgeBase(simpleRule);
        KieSession commandKSession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        assertTrue("There should be NO facts present in a new (empty) knowledge session.", commandKSession.getFactHandles().isEmpty());
        
        // Persist a facthandle to the database
        Integer integerFact = (new Random()).nextInt(Integer.MAX_VALUE-1) + 1;
        commandKSession.insert( integerFact );
       
        // At this point in the code, the fact has been persisted to the database
        //  (within a transaction via the SingleSessionCommandService) 
        Collection<FactHandle> factHandles =  commandKSession.getFactHandles();
        assertTrue("At least one fact should have been inserted by the ksession.insert() method above.", !factHandles.isEmpty());
        FactHandle origFactHandle = factHandles.iterator().next();
        assertTrue("The stored fact should contain the same number as the value inserted (but does not).", 
                Integer.parseInt(((DefaultFactHandle) origFactHandle).getObject().toString()) == integerFact.intValue() );
        
        // Save the sessionInfo id in order to retrieve it later
        long sessionInfoId = commandKSession.getIdentifier();
        
        // Clean up the session, environment, etc.
        PersistenceContextManager pcm = (PersistenceContextManager) commandKSession.getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
        commandKSession.dispose();
        pcm.dispose();
        db.close();
     
        // Reload session from the database
        DB newDb = MapDBPersistenceUtil.makeDB();
        context.put(MapDBEnvironmentName.DB_OBJECT, newDb);
        //context.put(EnvironmentName.TRANSACTION, new MapDBUserTransaction(newDb));
        env = createEnvironment();
       
        // Re-initialize the knowledge session:
        KieSession newCommandKSession = KieServices.Factory.get().getStoreServices().
        		loadKieSession(sessionInfoId, kbase, null, env);
       
        // Test that the session has been successfully reinitialized
        factHandles =  newCommandKSession.getFactHandles();
        assertTrue("At least one fact should have been persisted by the ksession.insert above.", 
                !factHandles.isEmpty() && factHandles.size() == 1);
        FactHandle retrievedFactHandle = factHandles.iterator().next();
        assertTrue("If the retrieved and original FactHandle object are the same, then the knowledge session has NOT been reloaded!", 
                origFactHandle != retrievedFactHandle);
        assertTrue("The retrieved fact should contain the same info as the original (but does not).", 
                Integer.parseInt(((DefaultFactHandle) retrievedFactHandle).getObject().toString()) == integerFact.intValue() );
        
        // Test to see if the (retrieved) facts can be processed
        ArrayList<Object>list = new ArrayList<Object>();
        newCommandKSession.setGlobal( "list", list );
        newCommandKSession.fireAllRules();
        assertEquals( 1, list.size() );
    }

    @Test @Ignore
    public void testListenersAfterSessionReload() {
        // https://bugzilla.redhat.com/show_bug.cgi?id=826952
        Environment env = createEnvironment();
        KieBase kbase = initializeKnowledgeBase(simpleRule);
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );

        ksession.addEventListener(new DefaultAgendaEventListener());
        ksession.addEventListener(new DefaultRuleRuntimeEventListener());

        assertEquals(1, ksession.getRuleRuntimeEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());

        ksession = KieServices.Factory.get().getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);

        assertEquals(1, ksession.getRuleRuntimeEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());
    }
}
