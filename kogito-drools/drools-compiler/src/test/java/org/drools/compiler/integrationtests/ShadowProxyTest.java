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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheesery;
import org.drools.compiler.Child;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.MockPersistentSet;
import org.drools.compiler.ObjectWithSet;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class ShadowProxyTest extends CommonTestMethodBase {

    @Test
    public void testShadowProxyInHierarchies() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ShadowProxyInHierarchies.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Child("gp"));
        ksession.fireAllRules();
    }

    @Test
    public void testShadowProxyOnCollections() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ShadowProxyOnCollections.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Cheesery cheesery = new Cheesery();
        ksession.insert(cheesery);

        ksession.fireAllRules();
        assertEquals(1, results.size());
        assertEquals(1, cheesery.getCheeses().size());
        assertEquals(results.get(0), cheesery.getCheeses().get(0));
    }

    @Test
    public void testShadowProxyOnCollections2() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ShadowProxyOnCollections2.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final List list = new ArrayList();
        list.add("example1");
        list.add("example2");

        final MockPersistentSet mockPersistentSet = new MockPersistentSet(false);
        mockPersistentSet.addAll(list);
        final ObjectWithSet objectWithSet = new ObjectWithSet();
        objectWithSet.setSet(mockPersistentSet);

        ksession.insert(objectWithSet);

        ksession.fireAllRules();

        assertEquals(1, results.size());
        assertEquals("show", objectWithSet.getMessage());
    }
}
