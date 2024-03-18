/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kie.api.runtime.ClassObjectFilter;
import org.drools.core.common.DefaultFactHandle;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.CheeseEqual;
import org.drools.mvel.compiler.Message;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class StatefulSessionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public StatefulSessionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDispose() throws Exception {
        final StringBuilder rule = new StringBuilder();
        rule.append("package org.drools.mvel.compiler\n");
        rule.append("rule X\n");
        rule.append("when\n");
        rule.append("    Message()\n");
        rule.append("then\n");
        rule.append("end\n");

        //building stuff
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule.toString());
        KieSession ksession = kbase.newKieSession();


        ksession.insert(new Message("test"));
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);

        ksession.dispose();

        try {
            // the following should raise an IllegalStateException as the session was already disposed
            ksession.fireAllRules();
            fail("An IllegallStateException should have been raised as the session was disposed before the method call.");
        } catch (final IllegalStateException ise) {
            // success
        }
    }

    @Test
    public void testGetStatefulKnowledgeSessions() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "../empty.drl");
        KieSession ksession_1 = kbase.newKieSession();

        final String expected_1 = "expected_1";
        final String expected_2 = "expected_2";
        final FactHandle handle_1 = ksession_1.insert(expected_1);
        final FactHandle handle_2 = ksession_1.insert(expected_2);
        ksession_1.fireAllRules();
        final Collection<? extends KieSession> coll_1 = kbase.getKieSessions();
        assertThat(coll_1.size() == 1).isTrue();

        final KieSession ksession_2 = coll_1.iterator().next();
        final Object actual_1 = ksession_2.getObject(handle_1);
        final Object actual_2 = ksession_2.getObject(handle_2);
        assertThat(actual_1).isEqualTo(expected_1);
        assertThat(actual_2).isEqualTo(expected_2);

        ksession_1.dispose();
        final Collection<? extends KieSession> coll_2 = kbase.getKieSessions();
        assertThat(coll_2.size() == 0).isTrue();

        // here to make sure it's safe to call dispose() twice
        ksession_1.dispose();
        final Collection<? extends KieSession> coll_3 = kbase.getKieSessions();
        assertThat(coll_3.size() == 0).isTrue();
    }

    @Test
    public void testGetFactHandle() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "../empty.drl");
        KieSession ksession = kbase.newKieSession();

        for (int i = 0; i < 20; i++) {
            final Object object = new Object();
            ksession.insert(object);
            final FactHandle factHandle = ksession.getFactHandle(object);
            assertThat(factHandle).isNotNull();
            assertThat(ksession.getObject(factHandle)).isEqualTo(object);
        }
        ksession.dispose();
    }

    @Test
    public void testGetFactHandleEqualityBehavior() throws Exception {
        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig);
        KieSession ksession = kbase.newKieSession();

        final CheeseEqual cheese = new CheeseEqual("stilton", 10);
        ksession.insert(cheese);
        final FactHandle fh = ksession.getFactHandle(new CheeseEqual("stilton", 10));
        assertThat(fh).isNotNull();
    }

    @Test
    public void testGetFactHandleIdentityBehavior() throws Exception {
        KieBaseTestConfiguration identityConfig = TestParametersUtil.getIdentityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", identityConfig);
        KieSession ksession = kbase.newKieSession();

        final CheeseEqual cheese = new CheeseEqual("stilton", 10);
        ksession.insert(cheese);
        final FactHandle fh1 = ksession.getFactHandle(new Cheese("stilton", 10));
        assertThat(fh1).isNull();
        final FactHandle fh2 = ksession.getFactHandle(cheese);
        assertThat(fh2).isNotNull();
    }

    @Test
    public void testDisconnectedFactHandle() {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        KieSession ksession = kbase.newKieSession();
        final DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert( "hello" );
        final DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert( "goodbye" );

        FactHandle key = DefaultFactHandle.createFromExternalFormat( helloHandle.toExternalForm() );
        assertThat(ksession.getObject(key)).isEqualTo("hello");

        key = DefaultFactHandle.createFromExternalFormat( goodbyeHandle.toExternalForm() );
        assertThat(ksession.getObject(key)).isEqualTo("goodbye");
    }

    @Test
    public void testIterateObjects() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_IterateObjects.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(new Cheese("stilton", 10));
        ksession.fireAllRules();

        final Iterator events = ksession.getObjects(new ClassObjectFilter(PersonInterface.class)).iterator();
        assertThat(events.hasNext()).isTrue();
        assertThat(results.size()).isEqualTo(1);
        assertThat(events.next()).isEqualTo(results.get(0));
    }
}
