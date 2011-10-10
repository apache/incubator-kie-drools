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

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.CommandFactory;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.FireAllRulesInterceptor;
import org.drools.command.impl.LoggingInterceptor;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPersistentStatefulSessionTest {

    private static Logger logger = LoggerFactory.getLogger(JpaPersistentStatefulSessionTest.class);

    private HashMap<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.tearDown(context);
    }

    @AfterClass
    public static void compareMarshallingData() {
        MarshallingTestUtil.compareMarshallingDataFromTest(DROOLS_PERSISTENCE_UNIT_NAME);
    }

    @Test
    public void testFactHandleSerialization() {
        String str = "";
        str += "package org.drools.test\n";
        str += "import java.util.concurrent.atomic.AtomicInteger\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += " $i: AtomicInteger(intValue > 0)\n";
        str += "then\n";
        str += " list.add( $i );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list", list);

        AtomicInteger value = new AtomicInteger(4);
        FactHandle atomicFH = ksession.insert(value);

        ksession.fireAllRules();

        assertEquals(1, list.size());

        value.addAndGet(1);
        ksession.update(atomicFH, value);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        String externalForm = atomicFH.toExternalForm();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getId(), kbase, null, env);

        atomicFH = ksession.execute(CommandFactory.fromExternalFactHandleCommand(externalForm));

        value.addAndGet(1);
        ksession.update(atomicFH, value);

        ksession.fireAllRules();

        list = (List<?>) ksession.getGlobal("list");

        assertEquals(3, list.size());

    }

    @Test
    public void testLocalTransactionPerStatement() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        ksession.fireAllRules();

        assertEquals(3, list.size());

    }

    @Test
    public void testUserTransactions() throws Exception {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  $i : Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( $i );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        ut.commit();

        List<?> list = new ArrayList<Object>();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ksession.fireAllRules();
        ut.commit();

        // insert and rollback
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(3);
        ut.rollback();

        // check we rolled back the state changes from the 3rd insert
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.fireAllRules();
        ut.commit();
        assertEquals(2, list.size());

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(3);
        ksession.insert(4);
        ut.commit();

        // rollback again, this is testing that we can do consecutive rollbacks
        // and commits without issue
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(5);
        ksession.insert(6);
        ut.rollback();

        ksession.fireAllRules();

        assertEquals(4, list.size());

        // now load the ksession
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getId(), kbase, null, env);

        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(7);
        ksession.insert(8);
        ut.commit();

        ksession.fireAllRules();

        assertEquals(6, list.size());
    }

    @Test
    public void testInterceptor() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        SingleSessionCommandService sscs = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.getWorkItemManager().completeWorkItem(0, null);
        assertEquals(3, list.size());
    }

    @Test
    public void testSetFocus() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "agenda-group \"badfocus\"";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();

        ksession.fireAllRules();

        assertEquals(3, list.size());
    }

}
