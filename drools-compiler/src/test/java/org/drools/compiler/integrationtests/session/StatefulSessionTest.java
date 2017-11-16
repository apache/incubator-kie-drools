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

package org.drools.compiler.integrationtests.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CheeseEqual;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.ClassObjectFilter;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StatefulSessionTest extends CommonTestMethodBase {

    @Test
    public void testDispose() throws Exception {
        final StringBuilder rule = new StringBuilder();
        rule.append("package org.drools.compiler\n");
        rule.append("rule X\n");
        rule.append("when\n");
        rule.append("    Message()\n");
        rule.append("then\n");
        rule.append("end\n");

        //building stuff
        final KieBase kbase = loadKnowledgeBaseFromString(rule.toString());
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Message("test"));
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);

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
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("../empty.drl"));

        final KieSession ksession_1 = createKnowledgeSession(kbase);
        final String expected_1 = "expected_1";
        final String expected_2 = "expected_2";
        final FactHandle handle_1 = ksession_1.insert(expected_1);
        final FactHandle handle_2 = ksession_1.insert(expected_2);
        ksession_1.fireAllRules();
        final Collection<? extends KieSession> coll_1 = kbase.getKieSessions();
        assertTrue(coll_1.size() == 1);

        final KieSession ksession_2 = coll_1.iterator().next();
        final Object actual_1 = ksession_2.getObject(handle_1);
        final Object actual_2 = ksession_2.getObject(handle_2);
        assertEquals(expected_1, actual_1);
        assertEquals(expected_2, actual_2);

        ksession_1.dispose();
        final Collection<? extends KieSession> coll_2 = kbase.getKieSessions();
        assertTrue(coll_2.size() == 0);

        // here to make sure it's safe to call dispose() twice
        ksession_1.dispose();
        final Collection<? extends KieSession> coll_3 = kbase.getKieSessions();
        assertTrue(coll_3.size() == 0);
    }

    @Test
    public void testGetFactHandle() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("../empty.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        for (int i = 0; i < 20; i++) {
            final Object object = new Object();
            ksession.insert(object);
            final FactHandle factHandle = ksession.getFactHandle(object);
            assertNotNull(factHandle);
            assertEquals(object, ksession.getObject(factHandle));
        }
        ksession.dispose();
    }

    @Test
    public void testGetFactHandleEqualityBehavior() throws Exception {
        final KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption(EqualityBehaviorOption.EQUALITY);
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc));
        final KieSession ksession = createKnowledgeSession(kbase);

        final CheeseEqual cheese = new CheeseEqual("stilton", 10);
        ksession.insert(cheese);
        final FactHandle fh = ksession.getFactHandle(new CheeseEqual("stilton", 10));
        assertNotNull(fh);
    }

    @Test
    public void testGetFactHandleIdentityBehavior() throws Exception {
        final KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption(EqualityBehaviorOption.IDENTITY);
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc));
        final KieSession ksession = createKnowledgeSession(kbase);

        final CheeseEqual cheese = new CheeseEqual("stilton", 10);
        ksession.insert(cheese);
        final FactHandle fh1 = ksession.getFactHandle(new Cheese("stilton", 10));
        assertNull(fh1);
        final FactHandle fh2 = ksession.getFactHandle(cheese);
        assertNotNull(fh2);
    }

    @Test
    public void testDisconnectedFactHandle() {
        final KieBase kbase = getKnowledgeBase();
        final KieSession ksession = createKnowledgeSession( kbase );
        final DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert( "hello" );
        final DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert( "goodbye" );

        FactHandle key = DefaultFactHandle.createFromExternalFormat( helloHandle.toExternalForm() );
        assertEquals( "hello",
                ksession.getObject( key ) );

        key = DefaultFactHandle.createFromExternalFormat( goodbyeHandle.toExternalForm() );
        assertEquals( "goodbye",
                ksession.getObject( key ) );
    }

    @Test
    public void testIterateObjects() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_IterateObjects.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(new Cheese("stilton", 10));
        ksession.fireAllRules();

        final Iterator events = ksession.getObjects(new ClassObjectFilter(PersonInterface.class)).iterator();
        assertTrue(events.hasNext());
        assertEquals(1, results.size());
        assertEquals(results.get(0), events.next());
    }
}
