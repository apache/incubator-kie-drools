/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteDumper;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.util.DroolsTestUtil.rulestoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class IndexingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public IndexingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test()
    public void testAlphaNodeSharing() {
        final String drl =
                "package org.drools.compiler.test\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule r1\n" +
                        "when\n" +
                        "   Person(name == \"Mark\")\n" +
                        "then\n" +
                        "end\n" +
                        "rule r2\n" +
                        "when\n" +
                        "   Person(name == \"Mark\", age == 40)\n" +
                        "then\n" +
                        "end\n" +
                        "rule r3\n" +
                        "when\n" +
                        "   Person(name == \"Mark\", age == 50)\n" +
                        "then\n" +
                        "end\n" +
                        "rule r4\n" +
                        "when\n" +
                        "   Person(name == \"John\", age == 60)\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final InternalWorkingMemory wm = (InternalWorkingMemory) kbase.newKieSession();
        try {
            final Map<String, Rule> rules = rulestoMap(kbase);

            final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
            assertNotNull(otn);
            assertEquals(2, otn.getObjectSinkPropagator().size());

            final AlphaNode a1 = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
            assertEquals(3, a1.getObjectSinkPropagator().size());
            assertEquals(3, a1.getAssociationsSize());
            assertTrue(a1.isAssociatedWith(rules.get("r1")));
            assertTrue(a1.isAssociatedWith(rules.get("r2")));
            assertTrue(a1.isAssociatedWith(rules.get("r3")));

            final AlphaNode a2 = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[1];
            assertEquals(1, a2.getAssociationsSize());
            assertEquals(1, a2.getObjectSinkPropagator().size());
            assertTrue(a2.isAssociatedWith(rules.get("r4")));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testBuildsIndexedAlphaNodes() {
        final String drl =
                "package org.drools.compiler.test\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule test1\n" +
                        "when\n" +
                        "   Person(name == \"Mark\", age == 37)\n" +
                        "   Person(name == \"Mark\", happy == true)\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final InternalWorkingMemory wm = (InternalWorkingMemory) kbase.newKieSession();
        try {
            final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
            assertNotNull(otn);
            final AlphaNode alphaNode1 = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
            final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) alphaNode1.getObjectSinkPropagator();
            final List<AlphaNode> hashableSinks = sinkAdapter.getHashableSinks();
            assertNotNull(hashableSinks);
            assertEquals(2, hashableSinks.size());

            final AlphaNode alphaNode2 = (AlphaNode) alphaNode1.getObjectSinkPropagator().getSinks()[0];
            assertSame(hashableSinks.get(0), alphaNode2);

            final AlphaNode alphaNode3 = (AlphaNode) alphaNode1.getObjectSinkPropagator().getSinks()[1];
            assertSame(hashableSinks.get(1), alphaNode3);
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testBuildsIndexedMemory() {
        // tests indexes are correctly built        
        final String drl =
                "package org.drools.compiler.test\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List list\n" +
                        "rule test1\n" +
                        "when\n" +
                        "   $p1  : Person($name : name )\n" +
                        "   $p2 : Person(name == $name)\n" + //indexed
                        "   $p3 : Person(name == $p1.name)\n" + //indexed
                        "   $p4 : Person(address.street == $p1.address.street)\n" + //not indexed
                        "   $p5 : Person(address.street == $p1.name)\n" + // indexed
                        "   $p6 : Person(addresses[0].street == $p1.name)\n" +  // indexed
                        "   $p7 : Person(name == $p1.address.street)\n" + //not indexed
                        "   $p8 : Person(addresses[0].street == null)\n" +  // not indexed
                        "then\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final InternalWorkingMemory wm = (InternalWorkingMemory) kbase.newKieSession();
        try {
            final ObjectTypeNode node = KieUtil.getObjectTypeNode(kbase, Person.class);
            assertNotNull(node);
            final LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getObjectSinkPropagator().getSinks()[0];
            final JoinNode j2 = (JoinNode) liaNode.getSinkPropagator().getSinks()[0];
            final JoinNode j3 = (JoinNode) j2.getSinkPropagator().getSinks()[0];
            final JoinNode j4 = (JoinNode) j3.getSinkPropagator().getSinks()[0];
            final JoinNode j5 = (JoinNode) j4.getSinkPropagator().getSinks()[0];
            final JoinNode j6 = (JoinNode) j5.getSinkPropagator().getSinks()[0];
            final JoinNode j7 = (JoinNode) j6.getSinkPropagator().getSinks()[0];
            final JoinNode j8 = (JoinNode) j7.getSinkPropagator().getSinks()[0];

            SingleBetaConstraints c = (SingleBetaConstraints) j2.getRawConstraints();
            assertTrue(c.isIndexed());
            BetaMemory bm = (BetaMemory) wm.getNodeMemory(j2);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            c = (SingleBetaConstraints) j3.getRawConstraints();
            assertTrue(c.isIndexed());
            bm = (BetaMemory) wm.getNodeMemory(j3);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            c = (SingleBetaConstraints) j4.getRawConstraints();
            assertFalse(c.isIndexed());
            bm = (BetaMemory) wm.getNodeMemory(j4);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleList);
            assertTrue(bm.getRightTupleMemory() instanceof TupleList);

            c = (SingleBetaConstraints) j5.getRawConstraints();
            assertTrue(c.isIndexed());
            bm = (BetaMemory) wm.getNodeMemory(j5);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            c = (SingleBetaConstraints) j6.getRawConstraints();
            assertTrue(c.isIndexed());
            bm = (BetaMemory) wm.getNodeMemory(j6);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            c = (SingleBetaConstraints) j7.getRawConstraints();
            assertFalse(c.isIndexed());
            bm = (BetaMemory) wm.getNodeMemory(j7);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleList);
            assertTrue(bm.getRightTupleMemory() instanceof TupleList);

            assertThat(j8.getRawConstraints()).isInstanceOf(EmptyBetaConstraints.class);
            bm = (BetaMemory) wm.getNodeMemory(j8);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleList);
            assertTrue(bm.getRightTupleMemory() instanceof TupleList);
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testIndexingOnQueryUnification() {
        final String drl =
                "package org.drools.compiler.test  \n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "query peeps( String $name, String $likes, String $street) \n" +
                        "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final InternalWorkingMemory wm = (InternalWorkingMemory) kbase.newKieSession();
        try {
            final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
            ObjectTypeNode node = null;
            for (final ObjectTypeNode n : nodes) {
                if (((ClassObjectType) n.getObjectType()).getClassType() == DroolsQuery.class) {
                    node = n;
                    break;
                }
            }

            assertNotNull(node);
            final AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
            final LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];
            final JoinNode j = (JoinNode) liaNode.getSinkPropagator().getSinks()[0]; // $p2

            final TripleNonIndexSkipBetaConstraints c = (TripleNonIndexSkipBetaConstraints) j.getRawConstraints();
            assertTrue(c.isIndexed());
            final BetaMemory bm = (BetaMemory) wm.getNodeMemory(j);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testIndexingOnQueryUnificationWithNot() {
        final String drl =
                "package org.drools.compiler.test  \n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "query peeps( String $name, int $age ) \n" +
                        "    not $p2 : Person( $name := name, age > $age ) \n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final StatefulKnowledgeSessionImpl wm = (StatefulKnowledgeSessionImpl) kbase.newKieSession();
        ReteDumper.dumpRete( wm );
        try {
            final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
            ObjectTypeNode node = null;
            for (final ObjectTypeNode n : nodes) {
                if (((ClassObjectType) n.getObjectType()).getClassType() == DroolsQuery.class) {
                    node = n;
                    break;
                }
            }

            assertNotNull(node);
            final AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
            final LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];

            final NotNode n = (NotNode) liaNode.getSinkPropagator().getSinks()[0];

            final DoubleNonIndexSkipBetaConstraints c = (DoubleNonIndexSkipBetaConstraints) n.getRawConstraints();
            assertTrue(c.isIndexed());
            final BetaMemory bm = (BetaMemory) wm.getNodeMemory(n);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            final Map<String, Integer> map = new HashMap<>();
            map.put("inserted", 0);
            map.put("deleted", 0);
            map.put("updated", 0);
            wm.openLiveQuery("peeps", new Object[]{Variable.v, 99}, new ViewChangedEventListener() {
                @Override
                public void rowInserted(final Row row) {
                    final Integer integer = map.get("inserted");
                    map.put("inserted", integer + 1);
                }

                @Override
                public void rowDeleted(final Row row) {
                    final Integer integer = map.get("deleted");
                    map.put("deleted", integer + 1);
                }

                @Override
                public void rowUpdated(final Row row) {
                    final Integer integer = map.get("updated");
                    map.put("updated", integer + 1);
                }
            });

            final Map<String, InternalFactHandle> peeps = new HashMap<>();

            Person p;
            InternalFactHandle fh;

            final int max = 3;

            // 1 matched, prior to any insertions
            assertInsertedUpdatedDeleted(map, 1, 0, 0);

            // x0 is the blocker
            for (int i = 0; i < max; i++) {
                p = new Person("x" + i, 100);
                fh = (InternalFactHandle) wm.insert(p);
                wm.fireAllRules();
                peeps.put(p.getName(), fh);
            }

            // insertions case 1 deletion
            assertInsertedUpdatedDeleted(map, 1, 0, 1);

            // each x is blocker in turn up to x99
            for (int i = 0; i < (max - 1); i++) {
                fh = peeps.get("x" + i);
                p = (Person) fh.getObject();
                p.setAge(90);
                wm.update(fh, p);
                wm.fireAllRules();
                assertEquals("i=" + i, 1, map.get("inserted").intValue()); // make sure this doesn't change
            }

            // no change
            assertInsertedUpdatedDeleted(map, 1, 0, 1);

            // x99 is still the blocker, everything else is just added
            for (int i = 0; i < (max - 1); i++) {
                fh = peeps.get("x" + i);
                p = (Person) fh.getObject();
                p.setAge(102);
                wm.update(fh, p);
                wm.fireAllRules();
                assertEquals("i=" + i, 1, map.get("inserted").intValue()); // make sure this doesn't change
            }

            // no change
            assertInsertedUpdatedDeleted(map, 1, 0, 1);

            // x99 is still the blocker
            for (int i = (max - 2); i >= 0; i--) {
                fh = peeps.get("x" + i);
                p = (Person) fh.getObject();
                p.setAge(90);
                wm.update(fh, p);
                wm.fireAllRules();
                assertEquals("i=" + i, 1, map.get("inserted").intValue()); // make sure this doesn't change
            }

            // move x99, should no longer be a blocker, now it can increase
            fh = peeps.get("x" + (max - 1));
            p = (Person) fh.getObject();
            p.setAge(90);
            wm.update(fh, p);
            wm.fireAllRules();
            assertEquals(2, map.get("inserted").intValue());
        } finally {
            wm.dispose();
        }
    }

    private void assertInsertedUpdatedDeleted(final Map<String, Integer> insertUpdateDeleteMap, final int expectedInserted,
                                              final int expectedUpdated, final int expectedDeleted) {
        assertEquals(expectedInserted, insertUpdateDeleteMap.get("inserted").intValue());
        assertEquals(expectedUpdated, insertUpdateDeleteMap.get("updated").intValue());
        assertEquals(expectedDeleted, insertUpdateDeleteMap.get("deleted").intValue());
    }

    @Test(timeout = 10000)
    public void testFullFastIteratorResume() {
        final String drl =
                "package org.drools.compiler.test  \n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "query peeps( String $name, int $age ) \n" +
                        "    not $p2 : Person( $name := name, age > $age ) \n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final StatefulKnowledgeSessionImpl wm = (StatefulKnowledgeSessionImpl) kbase.newKieSession();
        try {
            final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
            ObjectTypeNode node = null;
            for (final ObjectTypeNode n : nodes) {
                if (((ClassObjectType) n.getObjectType()).getClassType() == DroolsQuery.class) {
                    node = n;
                    break;
                }
            }

            assertNotNull(node);
            final AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
            final LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];

            final NotNode n = (NotNode) liaNode.getSinkPropagator().getSinks()[0];

            final DoubleNonIndexSkipBetaConstraints c = (DoubleNonIndexSkipBetaConstraints) n.getRawConstraints();
            assertTrue(c.isIndexed());
            final BetaMemory bm = (BetaMemory) wm.getNodeMemory(n);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);

            wm.openLiveQuery("peeps", new Object[]{Variable.v, 99}, new ViewChangedEventListener() {
                @Override
                public void rowInserted(final Row row) {
                }

                @Override
                public void rowDeleted(final Row row) {
                }

                @Override
                public void rowUpdated(final Row row) {
                }
            });


            Person p = new Person("x0", 100);
            wm.insert(p);

            for (int i = 1; i < 100; i++) {
                p = new Person("x" + i, 101);
                wm.insert(p);
                wm.fireAllRules();
            }

            final List<RightTuple> list = new ArrayList<>(100);
            FastIterator it = n.getRightIterator(bm.getRightTupleMemory());
            for (RightTuple rt = n.getFirstRightTuple(null, bm.getRightTupleMemory(), null, it); rt != null; rt = (RightTuple) it.next(rt)) {
                list.add(rt);
            }
            assertEquals(100, list.size());

            // check we can resume from each entry in the list above.
            for (int i = 0; i < 100; i++) {
                final RightTuple rightTuple = list.get(i);
                it = n.getRightIterator(bm.getRightTupleMemory(), rightTuple); // resumes from the current rightTuple
                int j = i + 1;
                for (RightTuple rt = (RightTuple) it.next(rightTuple); rt != null; rt = (RightTuple) it.next(rt)) {
                    assertSame(list.get(j), rt);
                    j++;
                }
            }
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testRangeIndex() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "when\n" +
                "   $s : String()" +
                "   exists Cheese( type > $s )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert("cheddar");
            ksession.insert("gorgonzola");
            ksession.insert("stilton");
            ksession.insert(new Cheese("gorgonzola", 10));
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testRangeIndex2() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "when\n" +
                "   $s : String()" +
                "   exists Cheese( type < $s )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert("gorgonzola");
            ksession.insert(new Cheese("cheddar", 10));
            ksession.insert(new Cheese("gorgonzola", 10));
            ksession.insert(new Cheese("stilton", 10));
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testNotNode() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 salience 10\n" +
                "when\n" +
                "   Person( $age : age )" +
                "   not Cheese( price < $age )\n" +
                "then\n" +
                "end\n" +
                "rule R2 salience 1\n" +
                "when\n" +
                "   $p : Person( age == 10 )" +
                "then\n" +
                "   modify($p) { setAge(15); }\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("mario", 10));
            ksession.insert(new Cheese("gorgonzola", 20));
            assertEquals(3, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testNotNodeModifyRight() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 salience 10 when\n" +
                "   Person( $age : age )\n" +
                "   not Cheese( price < $age )\n" +
                "then\n" +
                "end\n" +
                "rule R3 salience 5 when\n" +
                "   $c : Cheese( price == 8 )\n" +
                "then\n" +
                "   modify($c) { setPrice(15); }\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("A", 10));
            ksession.insert(new Cheese("C1", 20));
            ksession.insert(new Cheese("C2", 8));
            assertEquals(2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testRange() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 salience 10 when\n" +
                "   Person( $age : age, $doubleAge : doubleAge )\n" +
                "   not Cheese( price > $age && < $doubleAge )\n" +
                "then\n" +
                "end\n" +
                "rule R3 salience 5 when\n" +
                "   $c : Cheese( price == 15 )\n" +
                "then\n" +
                "   modify($c) { setPrice(8); }\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("A", 10));
            ksession.insert(new Cheese("C1", 30));
            ksession.insert(new Cheese("C2", 15));
            assertEquals(2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testRange2() throws IllegalAccessException, InstantiationException {
        final String drl = "package org.drools.compiler.test\n" +
                "declare A\n" +
                "    a: int\n" +
                "end\n" +
                "declare B\n" +
                "    b: int\n" +
                "end\n" +
                "declare C\n" +
                "    c: int\n" +
                "end\n" +
                "rule R1 when\n" +
                "   A( $a : a )\n" +
                "   B( $b : b )\n" +
                "   exists C( c > $a && < $b )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType aType = kbase.getFactType("org.drools.compiler.test", "A");
            final FactType bType = kbase.getFactType("org.drools.compiler.test", "B");
            final FactType cType = kbase.getFactType("org.drools.compiler.test", "C");

            final Object a1 = aType.newInstance();
            aType.set(a1, "a", 5);
            ksession.insert(a1);
            final Object a2 = aType.newInstance();
            aType.set(a2, "a", 11);
            ksession.insert(a2);

            final Object b1 = bType.newInstance();
            bType.set(b1, "b", 10);
            ksession.insert(b1);
            final Object b2 = bType.newInstance();
            bType.set(b2, "b", 6);
            ksession.insert(b2);

            final Object c = cType.newInstance();
            cType.set(c, "c", 7);
            ksession.insert(c);

            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testHashingAfterRemoveRightTuple() {
        // DROOLS-1326
        final String drl = "package " + this.getClass().getPackage().getName() + ";\n" +
                "import " + MyPojo.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "rule R1\n" +
                "when\n" +
                "    $my: MyPojo(\n" +
                "        vBoolean == true,\n" +
                "        $s : vString != \"y\",\n" +
                "        $l : vLong\n" +
                "    )\n" +
                "    not MyPojo(\n" +
                "        vBoolean == true,\n" +
                "        vString == $s,\n" +
                "        vLong != $l\n" +
                "    )\n" +
                "then\n" +
                "    list.add($my.getName());\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List<String> check = new ArrayList<>();
            session.setGlobal("list", check);

            final MyPojo a = new MyPojo("A", true, "x", 0);
            final MyPojo b = new MyPojo("B", true, "x", 7);
            final MyPojo c = new MyPojo("C", false, "y", 7);

            session.insert(a);
            final FactHandle fh_b = session.insert(b);
            final FactHandle fh_c = session.insert(c);

            session.fireAllRules();
            assertFalse(check.contains("A"));         // A should be blocked by B.

            c.setVBoolean(true);
            c.setVString("x");
            session.update(fh_c, c);

            b.setVBoolean(false);
            b.setVString("y");
            session.update(fh_b, b);

            session.fireAllRules();
            assertFalse(check.contains("A"));       // A is no longer blocked by B, *however* it is now blocked by C !
        } finally {
            session.dispose();
        }
    }

    public static class MyPojo {

        private final String name;
        private boolean vBoolean;
        private String vString;
        private long vLong;

        public MyPojo(final String name, final boolean vBoolean, final String vString, final long vLong) {
            this.name = name;
            this.vBoolean = vBoolean;
            this.vString = vString;
            this.vLong = vLong;
        }

        public String getName() {
            return name;
        }

        public boolean isVBoolean() {
            return vBoolean;
        }

        public String getVString() {
            return vString;
        }

        public long getVLong() {
            return vLong;
        }

        public void setVBoolean(final boolean vBoolean) {
            this.vBoolean = vBoolean;
        }

        public void setVString(final String vString) {
            this.vString = vString;
        }

        public void setVLong(final long vLong) {
            this.vLong = vLong;
        }

        @Override
        public String toString() {
            return "MyPojo: " + name;
        }
    }

    @Test
    public void testRequireLeftReorderingWithRangeIndex() {
        // DROOLS-1326
        final String drl = "import " + Queen.class.getCanonicalName() + ";\n"
                + "rule \"multipleQueensHorizontal\"\n"
                + "when\n"
                + "    Queen($id : id, row != null, $i : rowIndex)\n"
                + "    Queen(id > $id, rowIndex == $i)\n"
                + "then\n"
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final Queen queen1 = new Queen(1);
            final Queen queen2 = new Queen(2);
            final Integer row1 = 1;
            final Integer row2 = 2;

            final FactHandle fq1 = kieSession.insert(queen1);
            final FactHandle fq2 = kieSession.insert(queen2);
            // initially both queens have null row
            assertEquals(0, kieSession.fireAllRules());

            // now Q1 is the only queen on row1
            kieSession.update(fq1, queen1.setRow(row1));
            assertEquals(0, kieSession.fireAllRules());

            // Q1 moved to row2 but it's still alone
            kieSession.update(fq1, queen1.setRow(row2));
            assertEquals(0, kieSession.fireAllRules());

            // now Q2 is on row2 together with Q1 -> rule should fire
            kieSession.update(fq2, queen2.setRow(row2));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    public static class Queen {

        private final int id;
        private Integer row;

        public Queen(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Integer getRow() {
            return row;
        }

        public Queen setRow(final Integer row) {
            this.row = row;
            return this;
        }

        public int getRowIndex() {
            if (row == null) {
                return Integer.MIN_VALUE;
            }
            return row;
        }
    }

    @Test(timeout = 10000)
    public void testBuildsIndexedMemoryWithThis() {
        // tests indexes are correctly built
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "global java.util.List list\n" +
                           "rule test1\n" +
                           "when\n" +
                           "   $p1  : Person()\n" +
                           "   $p2 : String(this == $p1.name)\n" + //indexed
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("indexing-test", kieBaseTestConfiguration, drl);
        InternalWorkingMemory wm = (InternalWorkingMemory) kbase.newKieSession();

        try {
            final ObjectTypeNode node = KieUtil.getObjectTypeNode(wm.getKnowledgeBase(), Person.class);
            assertNotNull(node);
            final LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getObjectSinkPropagator().getSinks()[0];
            final JoinNode j2 = (JoinNode) liaNode.getSinkPropagator().getSinks()[0];

            SingleBetaConstraints c = (SingleBetaConstraints) j2.getRawConstraints();
            assertTrue(c.isIndexed());
            BetaMemory bm = (BetaMemory) wm.getNodeMemory(j2);
            assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable);
            assertTrue(bm.getRightTupleMemory() instanceof TupleIndexHashTable);
        } finally {
            wm.dispose();
        }
    }
}
