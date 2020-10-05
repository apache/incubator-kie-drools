/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.Collection;

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.SingleObjectSinkAdapter;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class AlphaNodeRangeIndexingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaNodeRangeIndexingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

        @Parameterized.Parameters(name = "KieBase type={0}")
        public static Collection<Object[]> getParameters() {
            return TestParametersUtil.getKieBaseCloudConfigurations(true);
        }

    @Test
    public void testInteger() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( age <= 4 )\n" +
                           "then\n end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn);
        final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertEquals(6, sinks.length);
        assertEquals(6, sinkAdapter.size());
        assertNull(sinkAdapter.getRangeIndexableSinks());
        assertEquals(6, sinkAdapter.getRangeIndexMap().entrySet().iterator().next().getValue().size());

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertEquals(5, fired);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertEquals(3, fired);
    }

    @Test
    public void testString() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( name >= \"Ann\" )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( name < \"Bob\" )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( name > \"Kent\" )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( name < \"Steve\" )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( name > \"John\" )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( name <= \"Paul\" )\n" +
                           "then\n end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn);
        final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertEquals(6, sinks.length);
        assertEquals(6, sinkAdapter.size());
        assertNull(sinkAdapter.getRangeIndexableSinks());
        assertEquals(6, sinkAdapter.getRangeIndexMap().entrySet().iterator().next().getValue().size());

        ksession.insert(new Person("John"));
        int fired = ksession.fireAllRules();
        assertEquals(3, fired);

        ksession.insert(new Person("Paul"));
        fired = ksession.fireAllRules();
        assertEquals(5, fired);
    }

    @Test
    public void testUnderThreshold() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn);
        final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertEquals(2, sinks.length);
        assertEquals(2, sinkAdapter.size());
        assertEquals(2, sinkAdapter.getRangeIndexableSinks().size()); // under threshold so not yet indexed
        assertNull(sinkAdapter.getRangeIndexMap());

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertEquals(2, fired);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertEquals(1, fired);
    }

    @Test
    public void testSurroundingRange() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 0 && < 20 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age >= 20 && < 40 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age >= 40 && < 60 )\n" +
                           "then\n end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn);
        final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertEquals(3, sinks.length);
        assertEquals(3, sinkAdapter.size());
        assertNull(sinkAdapter.getRangeIndexableSinks());
        assertEquals(3, sinkAdapter.getRangeIndexMap().entrySet().iterator().next().getValue().size());

        AlphaNode alphaNode1 = (AlphaNode) sinks[0];
        ObjectSinkPropagator objectSinkPropagator = alphaNode1.getObjectSinkPropagator();
        assertTrue(objectSinkPropagator instanceof SingleObjectSinkAdapter);
        ObjectSink objectSink = objectSinkPropagator.getSinks()[0];
        assertTrue(objectSink instanceof AlphaNode); // [age < 20] is the next single AlphaNode of [age >= 0]. Cannot be indexed.

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertEquals(0, fired);
    }

    @Test
    public void testRemoveObjectSink() {
        // The same rule as testInteger
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( age <= 4 )\n" +
                           "then\n end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession1 = kbase.newKieSession();

        ksession1.insert(new Person("John", 18));
        int fired1 = ksession1.fireAllRules();
        assertEquals(5, fired1);

        ksession1.insert(new Person("Paul", 60));
        fired1 = ksession1.fireAllRules();
        assertEquals(3, fired1);
        ksession1.dispose();

        // remove 2 rules
        kbase.removeRule("org.drools.compiler.test", "test2");
        kbase.removeRule("org.drools.compiler.test", "test3");

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn);
        final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertEquals(4, sinks.length);
        assertEquals(4, sinkAdapter.size());
        assertNull(sinkAdapter.getRangeIndexableSinks());
        assertEquals(4, sinkAdapter.getRangeIndexMap().entrySet().iterator().next().getValue().size()); // still above threshold

        final KieSession ksession2 = kbase.newKieSession();

        ksession2.insert(new Person("John", 18));
        int fired2 = ksession2.fireAllRules();
        assertEquals(3, fired2);

        ksession2.insert(new Person("Paul", 60));
        fired2 = ksession2.fireAllRules();
        assertEquals(2, fired2);
        ksession2.dispose();

        // remove 2 more rules
        kbase.removeRule("org.drools.compiler.test", "test4");
        kbase.removeRule("org.drools.compiler.test", "test5");

        final ObjectTypeNode otn2 = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertNotNull(otn2);
        final CompositeObjectSinkAdapter sinkAdapter2 = (CompositeObjectSinkAdapter) otn2.getObjectSinkPropagator();
        ObjectSink[] sinks2 = sinkAdapter2.getSinks();
        assertEquals(2, sinks2.length);
        assertEquals(2, sinkAdapter2.size());
        assertEquals(2, sinkAdapter2.getRangeIndexableSinks().size()); // under threshold so put back from rangeIndex
        assertNull(sinkAdapter2.getRangeIndexMap());

        final KieSession ksession3 = kbase.newKieSession();

        ksession3.insert(new Person("John", 18));
        int fired3 = ksession3.fireAllRules();
        assertEquals(1, fired3);

        ksession3.insert(new Person("Paul", 60));
        fired3 = ksession3.fireAllRules();
        assertEquals(1, fired3);
        ksession3.dispose();
    }

    @Test
    public void testModify() {
        // TODO
    }

    @Test
    public void testIncrementalCompilation() {
        // TODO
    }
}
