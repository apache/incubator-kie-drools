/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.persistence.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.compiler.Address;
import org.drools.compiler.Person;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.FireAllRulesInterceptor;
import org.drools.core.command.impl.LoggingInterceptor;
import org.drools.core.runtime.ChainableRunner;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.utils.KieHelper;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class JpaPersistentStatefulSessionWithOOPathTest {

    private static final boolean PESSIMISTIC_LOCKING = true;
    private static final boolean OPTIMISTIC_LOCKING = false;
    private static final boolean WITH_OOPATH = true;
    private static final boolean NO_OOPATH = false;

    private Map<String, Object> context;
    private Environment env;

    private final boolean locking;
    private final boolean oopath;

    public JpaPersistentStatefulSessionWithOOPathTest(final boolean locking, final boolean oopath) {
        this.locking = locking;
        this.oopath = oopath;
    }

    @Parameters(name = "pessimistic-locking={0}, with-oopath={1}")
    public static Collection<Object[]> persistence() {
        final Object[][] params = new Object[][]{
                {OPTIMISTIC_LOCKING, NO_OOPATH},
                {OPTIMISTIC_LOCKING, WITH_OOPATH},
                {PESSIMISTIC_LOCKING, NO_OOPATH},
                {PESSIMISTIC_LOCKING, WITH_OOPATH}
        };
        return Arrays.asList(params);
    }

    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if (locking) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
    }

    @After
    public void tearDown() throws Exception {
        DroolsPersistenceUtil.cleanUp(context);
    }

    @Test
    public void testFactHandleSerialization() {
        String str = "package org.kie.test\n" +
                     "import java.util.concurrent.atomic.AtomicInteger\n" +
                     "global java.util.List list\n" +
                     "rule rule1\n" +
                     "when\n" +
                     (oopath ? " AtomicInteger($i: /intValue{this > 0})\n" : " $i: AtomicInteger(intValue > 0)\n") +
                     "then\n" +
                     " list.add( $i );\n" +
                     "end\n";

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<>();

        ksession.setGlobal("list",
                           list);

        final AtomicInteger value = new AtomicInteger(4);
        FactHandle atomicFH = ksession.insert(value);

        ksession.fireAllRules();

        assertEquals(1,
                     list.size());

        value.addAndGet(1);
        ksession.update(atomicFH, value);
        ksession.fireAllRules();

        assertEquals(2,
                     list.size());
        final String externalForm = atomicFH.toExternalForm();

        ksession = KieServices.get().getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);

        atomicFH = ksession.execute(CommandFactory.fromExternalFactHandleCommand(externalForm));

        value.addAndGet(1);
        ksession.update(atomicFH, value);

        ksession.fireAllRules();

        list = (List<?>) ksession.getGlobal("list");

        assertEquals(3, list.size());

    }

    @Test
    public void testLocalTransactionPerStatement() {
        final String str = getSimpleRule();

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final List<?> list = new ArrayList<>();

        ksession.setGlobal("list",
                           list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        ksession.fireAllRules();

        assertEquals(3,
                     list.size());

    }

    @Test
    public void testUserTransactions() throws Exception {
        String str = "package org.kie.test\n" +
                     "global java.util.List list\n" +
                     "rule rule1\n" +
                     "when\n" +
                     (oopath ? " $i: Integer( /intValue{this > 0})\n" : " $i : Integer(intValue > 0)\n") +
                     "then\n" +
                     " list.add( $i );\n" +
                     "end\n";

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        ut.commit();

        final List<?> list = new ArrayList<>();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.setGlobal("list",
                           list);
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
        assertEquals(2,
                     list.size());

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(3);
        ksession.insert(4);
        ut.commit();

        // rollback again, this is testing that we can do consecutive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(5);
        ksession.insert(6);
        ut.rollback();

        ksession.fireAllRules();

        assertEquals(4,
                     list.size());

        // now load the ksession
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);

        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.insert(7);
        ksession.insert(8);
        ut.commit();

        ksession.fireAllRules();

        assertEquals(6,
                     list.size());
    }

    @Test
    public void testInterceptor() {
        final String str = getSimpleRule();

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final PersistableRunner sscs = (PersistableRunner)
                ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());
        final List<?> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.getWorkItemManager().completeWorkItem(0, null);
        assertEquals(3, list.size());
    }

    @Test
    public void testInterceptorOnRollback() throws Exception {
        final String str = getSimpleRule();

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final PersistableRunner sscs = (PersistableRunner)
                ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());

        ChainableRunner runner = sscs.getChainableRunner();

        assertEquals(LoggingInterceptor.class, runner.getClass());
        runner = (ChainableRunner) runner.getNext();
        assertEquals(FireAllRulesInterceptor.class, runner.getClass());
        runner = (ChainableRunner) runner.getNext();
        assertEquals(LoggingInterceptor.class, runner.getClass());

        final UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        final List<?> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ut.rollback();

        ksession.insert(3);

        runner = sscs.getChainableRunner();

        assertEquals(LoggingInterceptor.class, runner.getClass());
        runner = (ChainableRunner) runner.getNext();
        assertEquals(FireAllRulesInterceptor.class, runner.getClass());
        runner = (ChainableRunner) runner.getNext();
        assertEquals(LoggingInterceptor.class, runner.getClass());

    }

    @Test
    public void testSetFocus() {
        String str;
        if (oopath) {
            str = "package org.kie.test\n" +
                  "global java.util.List list\n" +
                  "rule rule1\n" +
                  "agenda-group \"badfocus\"when\n" +
                  "  Integer(/intValue{this > 0})\n" +
                  "then\n" +
                  "  list.add( 1 );\n" +
                  "end\n";
        } else {
            str = "package org.kie.test\n" +
                  "global java.util.List list\n" +
                  "rule rule1\n" +
                  "agenda-group \"badfocus\"" +
                  "when\n" +
                  "  Integer(intValue > 0)\n" +
                  "then\n" +
                  "  list.add( 1 );\n" +
                  "end\n";
        }

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final List<?> list = new ArrayList<>();

        ksession.setGlobal("list",
                           list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();

        ksession.fireAllRules();

        assertEquals(3,
                     list.size());
    }

    @Test
    public void testCreateAndDestroySession() {
        final String str = getSimpleRule();

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final List<?> list = new ArrayList<>();

        ksession.setGlobal("list",
                           list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        ksession.fireAllRules();

        assertEquals(3, list.size());

        final long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        try {
            JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
            fail("There should not be any session with id " + ksessionId);
        } catch (final Exception e) {

        }
    }

    @Test
    public void testCreateAndDestroyNonPersistentSession() {
        final String str = getSimpleRule();

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = kbase.newKieSession();
        final List<?> list = new ArrayList<>();

        ksession.setGlobal("list",
                           list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        ksession.fireAllRules();

        assertEquals(3, list.size());

        final long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        try {
            ksession.fireAllRules();
            fail("Session should already be disposed " + ksessionId);
        } catch (final IllegalStateException e) {

        }
    }

    @Test
    public void testFromNodeWithModifiedCollection() {
        // DROOLS-376
        String str;
        if (oopath) {
            str = "package org.drools.test\n" +
                   "import org.drools.compiler.Person\n" +
                   "import org.drools.compiler.Address\n" +
                   "rule rule1\n" +
                   "when\n" +
                   " $p: Person($list : addresses, /addresses{street == \"y\"})\n" +
                   "then\n" +
                   " $list.add( new Address(\"z\") );\n" +
                   " $list.add( new Address(\"w\") );\n" +
                   "end\n";
        } else {
            str = "package org.drools.test\n" +
                  "import org.drools.compiler.Person\n" +
                  "import org.drools.compiler.Address\n" +
                  "rule rule1\n" +
                  "when\n" +
                  " $p: Person($list : addresses)\n" +
                  " $a: Address(street == \"y\") from $list\n" +
                  "then\n" +
                  " $list.add( new Address(\"z\") );\n" +
                  " $list.add( new Address(\"w\") );\n" +
                  "end\n";
        }

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession( kbase, null, env );
        final long sessionId = ksession.getIdentifier();

        final Person p1 = new Person("John");
        p1.addAddress(new Address("x"));
        p1.addAddress(new Address("y"));

        ksession.insert(p1);

        ksession.fireAllRules();

        assertEquals(4,
                     p1.getAddresses().size());

        ksession.dispose();

        // Should not fail here
        KieServices.get().getStoreServices().loadKieSession(sessionId, kbase, null, env);
    }

    private String getSimpleRule() {
        if (oopath) {
            return "package org.kie.test\n" +
                   "global java.util.List list\n" +
                   "rule rule1\n" +
                   "when\n" +
                   "  Integer(/intValue{this > 0})\n" +
                   "then\n" +
                   "  list.add( 1 );\n" +
                   "end\n" +
                   "\n";

        } else {
            return "package org.kie.test\n" +
                   "global java.util.List list\n" +
                   "rule rule1\n" +
                   "when\n" +
                   "  Integer(intValue > 0)\n" +
                   "then\n" +
                   "  list.add( 1 );\n" +
                   "end\n";
        }
    }
}
