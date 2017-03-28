/* * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.compiler.Address;
import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
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
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.*;
import static org.drools.persistence.util.DroolsPersistenceUtil.*;

@RunWith(Parameterized.class)
public class JpaPersistentStatefulSessionTest {

    private Map<String, Object> context;
    private Environment env;
    private final boolean locking;

    @Parameters(name = "{0}")
    public static Collection<Object[]> persistence() {
        final Object[][] locking = new Object[][]{
                {OPTIMISTIC_LOCKING},
                {PESSIMISTIC_LOCKING}
        };
        return Arrays.asList(locking);
    }

    public JpaPersistentStatefulSessionTest(final String locking) {
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
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
        factHandleSerialization(false);
    }

    @Test
    public void testFactHandleSerializationWithOOPath() {
        factHandleSerialization(true);
    }

    private void factHandleSerialization(final boolean withOOPath) {
        final String str = "package org.kie.test\n" +
                "import java.util.concurrent.atomic.AtomicInteger\n" +
                "global java.util.List list\n" +
                "rule rule1\n" +
                "when\n" +
                (withOOPath ? " AtomicInteger($i: /intValue{this > 0})\n" : " $i: AtomicInteger(intValue > 0)\n") +
                "then\n" +
                " list.add( $i );\n" +
                "end\n" +
                "\n";

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        List<AtomicInteger> list = new ArrayList<>();

        ksession.setGlobal("list", list);

        final AtomicInteger value = new AtomicInteger(4);
        FactHandle atomicFH = ksession.insert(value);

        ksession.fireAllRules();

        assertThat(list).hasSize(1);

        value.addAndGet(1);
        ksession.update(atomicFH, value);
        ksession.fireAllRules();

        assertThat(list).hasSize(2);
        final String externalForm = atomicFH.toExternalForm();

        ksession = KieServices.get().getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);

        atomicFH = ksession.execute(CommandFactory.fromExternalFactHandleCommand(externalForm));

        value.addAndGet(1);
        ksession.update(atomicFH, value);

        ksession.fireAllRules();

        list = (List<AtomicInteger>) ksession.getGlobal("list");

        assertThat(list).hasSize(3);
    }

    @Test
    public void testLocalTransactionPerStatement() {
        localTransactionPerStatement(false);
    }

    @Test
    public void testLocalTransactionPerStatementWithOOPath() {
        localTransactionPerStatement(true);
    }

    private void localTransactionPerStatement(final boolean withOOPath) {
        final String rule = getSimpleRule(withOOPath);

        final KieBase kbase = new KieHelper().addContent(rule, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final List<Integer> list = new ArrayList<>();

        ksession.setGlobal("list", list);

        insertIntRange(ksession, 1, 3);

        ksession.fireAllRules();

        assertThat(list).hasSize(3);
    }

    @Test
    public void testUserTransactions() throws Exception {
        userTransactions(false);
    }

    @Test
    public void testUserTransactionsWithOOPath() throws Exception {
        userTransactions(true);
    }

    private void userTransactions(final boolean withOOPath) throws Exception {
        final String str = "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule rule1\n" +
                "when\n" +
                (withOOPath ? " $i: Integer( /intValue{this > 0})\n" : " $i : Integer(intValue > 0)\n") +
                "then\n" +
                " list.add( $i );\n" +
                "end\n";

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        ut.commit();

        final List<Integer> list = new ArrayList<>();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ksession.setGlobal("list", list);
        insertIntRange(ksession, 1, 2);
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
        assertThat(list).hasSize(2);

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        insertIntRange(ksession, 3, 4);
        ut.commit();

        // rollback again, this is testing that we can do consecutive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        insertIntRange(ksession, 5, 6);
        ut.rollback();

        ksession.fireAllRules();

        assertThat(list).hasSize(4);

        // now load the ksession
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);

        ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();

        insertIntRange(ksession, 7, 8);
        ut.commit();

        ksession.fireAllRules();

        assertThat(list).hasSize(6);
    }

    @Test
    public void testInterceptor() {
        interceptor(false);
    }

    @Test
    public void testInterceptorWithOOPath() {
        interceptor(true);
    }

    private void interceptor(final boolean withOOPath) {
        final String str = getSimpleRule(withOOPath);

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final PersistableRunner sscs = (PersistableRunner) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());
        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        insertIntRange(ksession, 1, 3);
        ksession.getWorkItemManager().completeWorkItem(0, null);
        assertThat(list).hasSize(3);
    }

    @Test
    public void testInterceptorOnRollback() throws Exception {
        interceptorOnRollback(false);
    }

    @Test
    public void testInterceptorOnRollbackWithOOPAth() throws Exception {
        interceptorOnRollback(true);
    }

    private void interceptorOnRollback(final boolean withOOPath) throws Exception {
        final String str = getSimpleRule(withOOPath);

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final PersistableRunner sscs = (PersistableRunner) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());

        ChainableRunner runner = sscs.getChainableRunner();

        assertThat(runner.getClass()).isEqualTo(LoggingInterceptor.class);
        runner = (ChainableRunner) runner.getNext();
        assertThat(runner.getClass()).isEqualTo(FireAllRulesInterceptor.class);
        runner = (ChainableRunner) runner.getNext();
        assertThat(runner.getClass()).isEqualTo(LoggingInterceptor.class);

        final UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        final List<?> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ut.rollback();

        ksession.insert(3);

        runner = sscs.getChainableRunner();

        assertThat(runner.getClass()).isEqualTo(LoggingInterceptor.class);
        runner = (ChainableRunner) runner.getNext();
        assertThat(runner.getClass()).isEqualTo(FireAllRulesInterceptor.class);
        runner = (ChainableRunner) runner.getNext();
        assertThat(runner.getClass()).isEqualTo(LoggingInterceptor.class);

    }

    @Test
    public void testSetFocus() {
        testFocus(false);
    }

    @Test
    public void testSetFocusWithOOPath() {
        testFocus(true);
    }

    private void testFocus(final boolean withOOPath) {
        final String str = "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule rule1\n" +
                "agenda-group \"badfocus\"" +
                "when\n" +
                (withOOPath ? "  Integer(/intValue{this > 0})\n" : "  Integer(intValue > 0)\n") +
                "then\n" +
                "  list.add( 1 );\n" +
                "end\n" +
                "\n";

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final List<Integer> list = new ArrayList<>();

        ksession.setGlobal("list", list);

        insertIntRange(ksession, 1, 3);
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();

        ksession.fireAllRules();

        assertThat(list).hasSize(3);
    }

    @Test
    public void testSharedReferences() {
        final KieBase kbase = new KieHelper().getKieContainer().getKieBase();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);

        final Person x = new Person("test");
        final List<Person> test = new ArrayList<>();
        final List<Person> test2 = new ArrayList<>();
        test.add(x);
        test2.add(x);

        assertThat(test.get(0)).isSameAs(test2.get(0));

        ksession.insert(test);
        ksession.insert(test2);
        ksession.fireAllRules();

        final StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);

        final Iterator c = ksession2.getObjects().iterator();
        final List ref1 = (List) c.next();
        final List ref2 = (List) c.next();

        assertThat(ref1.get(0)).isSameAs(ref2.get(0));

    }

    @Test
    public void testMergeConfig() {
        // JBRULES-3155
        final KieBase kbase = new KieHelper().getKieContainer().getKieBase();

        final Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "com.example.CustomJPAProcessInstanceManagerFactory");
        final KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, config, env);
        final SessionConfiguration sessionConfig = (SessionConfiguration) ksession.getSessionConfiguration();

        assertThat(sessionConfig.getProcessInstanceManagerFactory()).isEqualTo("com.example.CustomJPAProcessInstanceManagerFactory");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroySession() {
        createAndDestroySession(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroySessionWithOOPath() {
        createAndDestroySession(true);
    }

    public void createAndDestroySession(final boolean withOOPath) {
        final String str = getSimpleRule(withOOPath);

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final List<Integer> list = new ArrayList<>();

        ksession.setGlobal("list", list);

        insertIntRange(ksession, 1, 3);

        ksession.fireAllRules();

        assertThat(list).hasSize(3);

        final long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
        fail("There should not be any session with id " + ksessionId);

    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroyNonPersistentSession() {
        createAndDestroyNonPersistentSession(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroyNonPersistentSessionWithOOPath() {
        createAndDestroyNonPersistentSession(true);
    }

    private void createAndDestroyNonPersistentSession(final boolean withOOPath) {
        final String str = getSimpleRule(withOOPath);

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = kbase.newKieSession();
        final List<Integer> list = new ArrayList<>();

        ksession.setGlobal("list",
                list);

        insertIntRange(ksession, 1, 3);

        ksession.fireAllRules();

        assertThat(list).hasSize(3);

        final long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        ksession.fireAllRules();
        fail("Session should already be disposed " + ksessionId);
    }

    @Test
    public void testFromNodeWithModifiedCollection() {
        fromNodeWithModifiedCollection(false);
    }

    @Test
    public void testFromNodeWithModifiedCollectionWithOOPath() {
        fromNodeWithModifiedCollection(true);
    }

    private void fromNodeWithModifiedCollection(final boolean withOOPath) {
        // DROOLS-376
        final String str = "package org.drools.test\n" +
                "import org.drools.compiler.Person\n" +
                "import org.drools.compiler.Address\n" +
                "rule rule1\n" +
                "when\n" +
                (withOOPath ?
                        " $p: Person($list : addresses, /addresses{street == \"y\"})\n" :
                        " $p: Person($list : addresses)\n" + " $a: Address(street == \"y\") from $list\n"
                ) +
                "then\n" +
                " $list.add( new Address(\"z\") );\n" +
                " $list.add( new Address(\"w\") );\n" +
                "end\n";

        final KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();

        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);

        final Person p1 = new Person("John");
        p1.addAddress(new Address("x"));
        p1.addAddress(new Address("y"));

        ksession.insert(p1);

        ksession.fireAllRules();

        assertThat(p1.getAddresses()).hasSize(4);

        ksession.dispose();

        // Should not fail here
    }

    private String getSimpleRule(final boolean withOOPath) {
        return "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule rule1\n" +
                "when\n" +
                (withOOPath ? "  Integer(/intValue{this > 0})\n" : "  Integer(intValue > 0)\n") +
                "then\n" +
                "  list.add( 1 );\n" +
                "end\n" +
                "\n";
    }

    /**
     * Insert integer range into session
     *
     * @param ksession Session to insert ints in
     * @param from start of the range of ints to be inserted to ksession (inclusive)
     * @param to end of the range of ints to be inserted to ksession (inclusive)
     */
    private void insertIntRange(final KieSession ksession, final int from, final int to){
        IntStream.rangeClosed(from, to).forEach(ksession::insert);
    }
}
