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
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class DroolsFromRHSTest extends CommonTestMethodBase {

    @Test
    public void testHalt() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_halt.drl" ) );
        final KieSession ksession = createKnowledgeSession( kbase );

        final List results = new ArrayList();
        ksession.setGlobal( "results",
                results );

        ksession.insert(0);
        ksession.fireAllRules();

        assertEquals( 10,
                results.size() );
        for ( int i = 0; i < 10; i++ ) {
            assertEquals(i,
                    results.get( i ) );
        }
    }

    @Test
    public void testFireLimit() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_fireLimit.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(0);
        int count = ksession.fireAllRules();
        assertEquals(21, count);

        assertEquals(20, results.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, results.get(i));
        }
        results.clear();

        ksession.insert(0);
        count = ksession.fireAllRules(10);
        assertEquals(10, count);

        assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, results.get(i));
        }

        count = ksession.fireAllRules(); //should finish the rest
        assertEquals(11, count);
        assertEquals(20, results.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, results.get(i));
        }
        results.clear();

        ksession.insert(0);
        count = ksession.fireAllRules();

        assertEquals(21, count);

        assertEquals(20, results.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, results.get(i));
        }
        results.clear();
    }
}
