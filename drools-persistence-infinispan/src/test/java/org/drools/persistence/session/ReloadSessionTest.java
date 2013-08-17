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

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import org.drools.core.common.DefaultFactHandle;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.util.PersistenceUtil;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultWorkingMemoryEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.infinispan.InfinispanKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class ReloadSessionTest {

    // Datasource (setup & clean up)
    private HashMap<String, Object> context;
    private DefaultCacheManager cm;

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
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        cm = (DefaultCacheManager) context.get(ENTITY_MANAGER_FACTORY);
    }

    @After
    public void cleanUp() {
        PersistenceUtil.tearDown(context);
    }

    private KnowledgeBase initializeKnowledgeBase(String rule) { 
        // Initialize knowledge base/session/etc..
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
       
        return kbase;
    }
    
    @Test 
    public void reloadKnowledgeSessionTest() { 
        
        // Initialize drools environment stuff
        Environment env = createEnvironment(context);
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession commandKSession = InfinispanKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
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
        int sessionInfoId = commandKSession.getId();
        
        // Clean up the session, environment, etc.
        PersistenceContextManager pcm = (PersistenceContextManager) commandKSession.getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
        commandKSession.dispose();
        pcm.dispose();
     
        // Reload session from the cache
        env = createEnvironment(context);
       
        // Re-initialize the knowledge session:
        StatefulKnowledgeSession newCommandKSession
            = InfinispanKnowledgeService.loadStatefulKnowledgeSession(sessionInfoId, kbase, null, env);
       
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
        Environment env = createEnvironment(context);
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession ksession = InfinispanKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );

        ksession.addEventListener(new DefaultAgendaEventListener());
        ksession.addEventListener(new DefaultWorkingMemoryEventListener());

        assertEquals(1, ksession.getWorkingMemoryEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());

        ksession = InfinispanKnowledgeService.loadStatefulKnowledgeSession(ksession.getId(), kbase, null, env);

        assertEquals(1, ksession.getWorkingMemoryEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());
    }
}
