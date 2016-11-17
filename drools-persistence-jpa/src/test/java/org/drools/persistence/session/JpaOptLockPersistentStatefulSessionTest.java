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
package org.drools.persistence.session;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.jpa.OptimisticLockRetryInterceptor;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.hibernate.StaleObjectStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class JpaOptLockPersistentStatefulSessionTest {

    private static Logger logger = LoggerFactory.getLogger(JpaOptLockPersistentStatefulSessionTest.class);

    private Map<String, Object> context;
    private Environment env;

    public JpaOptLockPersistentStatefulSessionTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }
        
    @After
    public void tearDown() throws Exception {
        DroolsPersistenceUtil.cleanUp(context);
    }

    @Test
    public void testOptimisticLockInterceptor() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();
        for (int i = 0; i < 2; i++) {
            new InsertAndFireThread(ksession.getIdentifier(), kbase, list).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals( 6, list.size() );
        ksession.dispose();
    }

    private class InsertAndFireThread extends Thread {

        private long ksessionId;
        private KnowledgeBase kbase;
        private List<?> list;

        InsertAndFireThread(long ksessionId, KnowledgeBase kbase, List<?> list) {
            this.ksessionId = ksessionId;
            this.kbase = kbase;
            this.list = list;
        }

        @Override
        public void run() {
            StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, createEnvironment(context) );
            PersistableRunner sscs = (PersistableRunner)((CommandBasedStatefulKnowledgeSession) ksession2).getRunner();
            OptimisticLockRetryInterceptor interceptor = new OptimisticLockRetryInterceptor();
            // set higher delay so that the interceptor is not invoked multiple times on slow machines
            interceptor.setDelay(500);
            sscs.addInterceptor(interceptor);

            ksession2.setGlobal( "list", list );
            ksession2.insert( 1 );
            ksession2.insert( 2 );
            ksession2.insert( 3 );
            ksession2.getWorkItemManager().completeWorkItem(0, null);
            ksession2.fireAllRules();
            logger.info("The above " + StaleObjectStateException.class.getSimpleName() + "'s were expected in this test.");

            ksession2.dispose();
        }
    }
}
