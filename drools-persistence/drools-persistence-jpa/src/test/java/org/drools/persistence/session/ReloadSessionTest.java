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

import org.drools.core.common.DefaultFactHandle;
import org.drools.persistence.api.PersistenceContextManager;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

@RunWith(Parameterized.class)
public class ReloadSessionTest {

    // Datasource (setup & clean up)
    private Map<String, Object> context;
    private EntityManagerFactory emf;
    private boolean locking;

    private static String simpleRule = "package org.kie.test\n"
            + "global java.util.List list\n" 
            + "rule rule1\n" 
            + "when\n"
            + "  Integer(intValue > 0)\n" 
            + "then\n" 
            + "  list.add( 1 );\n"
            + "end\n" 
            + "\n";


    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public ReloadSessionTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @Before
    public void setup() {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);

    }

    @After
    public void cleanUp() {
        DroolsPersistenceUtil.cleanUp(context);
    }

    private Environment createEnvironment() { 
        Environment env = DroolsPersistenceUtil.createEnvironment(context);
        if( locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return env;
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
        Environment env = createEnvironment();
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession commandKSession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        assertTrue("There should be NO facts present in a new (empty) knowledge session.", commandKSession.getFactHandles().isEmpty());
        
        // Persist a facthandle to the database
        Integer integerFact = (new Random()).nextInt(Integer.MAX_VALUE-1) + 1;
        commandKSession.insert( integerFact );
       
        // At this point in the code, the fact has been persisted to the database
        //  (within a transaction via the PersistableRunner)
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
        emf.close();
     
        // Reload session from the database
        emf = Persistence.createEntityManagerFactory(DROOLS_PERSISTENCE_UNIT_NAME);
        context.put(ENTITY_MANAGER_FACTORY, emf);
        env = createEnvironment();
       
        // Re-initialize the knowledge session:
        StatefulKnowledgeSession newCommandKSession
            = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionInfoId, kbase, null, env);
       
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
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );

        ksession.addEventListener(new DefaultAgendaEventListener());
        ksession.addEventListener(new DefaultRuleRuntimeEventListener());

        assertEquals(1, ksession.getRuleRuntimeEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);

        assertEquals(1, ksession.getRuleRuntimeEventListeners().size());
        assertEquals(1, ksession.getAgendaEventListeners().size());
    }
}
