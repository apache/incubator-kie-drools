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
package org.drools.mvel.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.common.TupleSets;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.drools.tms.TruthMaintenanceSystemFactoryImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryLeakTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testStagedTupleLeak(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // BZ-1056599
        String str =
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    insertLogical( $i.toString() );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    delete( $i );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    $l : Long()\n" +
                "    $s : String( this == $l.toString() )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        TruthMaintenanceSystemFactoryImpl tms = (TruthMaintenanceSystemFactoryImpl) TruthMaintenanceSystemFactory.get();
        tms.clearEntryPointsMap();

        for ( int i = 0; i < 10; i++ ) {
            ksession.insert( i );
            ksession.fireAllRules();
        }

        Rete rete = ( (InternalRuleBase) kbase ).getRete();
        JoinNode joinNode = null;
        for ( ObjectTypeNode otn : rete.getObjectTypeNodes() ) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode) otn.getObjectSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertThat(joinNode).isNotNull();
        InternalWorkingMemory wm                = (InternalWorkingMemory) ksession;
        BetaMemory memory            = (BetaMemory) wm.getNodeMemory(joinNode);
        TupleSets stagedRightTuples = memory.getStagedRightTuples();
        assertThat(stagedRightTuples.getDeleteFirst()).isNull();
        assertThat(stagedRightTuples.getInsertFirst()).isNull();

        // DROOLS-6809
        assertThat(tms.getEntryPointsMapSize()).isEqualTo(1);
        ksession.dispose();
        assertThat(tms.getEntryPointsMapSize()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testStagedLeftTupleLeak(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // BZ-1058874
        String str =
                "rule R1 when\n" +
                "    String( this == \"this\" )\n" +
                "    String( this == \"that\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();

        for ( int i = 0; i < 10; i++ ) {
            FactHandle fh = ksession.insert( "this" );
            ksession.fireAllRules();
            ksession.delete( fh );
            ksession.fireAllRules();
        }

        Rete rete = ( (InternalRuleBase) kbase ).getRete();
        LeftInputAdapterNode liaNode = null;
        for ( ObjectTypeNode otn : rete.getObjectTypeNodes() ) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                AlphaNode alphaNode = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
                liaNode = (LeftInputAdapterNode) alphaNode.getObjectSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertThat(liaNode).isNotNull();
        InternalWorkingMemory wm = (InternalWorkingMemory) ksession;
        LeftInputAdapterNode.LiaNodeMemory memory = wm.getNodeMemory(liaNode);
        TupleSets stagedLeftTuples = memory.getSegmentMemory().getStagedLeftTuples();
        assertThat(stagedLeftTuples.getDeleteFirst()).isNull();
        assertThat(stagedLeftTuples.getInsertFirst()).isNull();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testBetaMemoryLeakOnFactDelete(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-913
        String drl =
                "rule R1 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "    $c : Integer(this == 2)\n" +
                "then \n" +
                "end\n" +
                "rule R2 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "    $c : Integer(this == 3)\n" +
                "then \n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        FactHandle fh1 = ksession.insert( 1 );
        FactHandle fh2 = ksession.insert( 3 );
        FactHandle fh3 = ksession.insert( "test" );
        ksession.fireAllRules();

        ksession.delete( fh1 );
        ksession.delete( fh2 );
        ksession.delete( fh3 );
        ksession.fireAllRules();

        NodeMemories nodeMemories = ( (InternalWorkingMemory) ksession ).getNodeMemories();
        for ( int i = 0; i < nodeMemories.length(); i++ ) {
            Memory memory = nodeMemories.peekNodeMemory( i );
            if ( memory != null && memory.getSegmentMemory() != null ) {
                SegmentMemory segmentMemory = memory.getSegmentMemory();
                System.out.println( memory );
                TupleImpl deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();
                if ( segmentMemory.getRootNode() instanceof JoinNode ) {
                    BetaMemory bm = (BetaMemory) segmentMemory.getNodeMemories()[0];
                    assertThat(bm.getLeftTupleMemory().size()).isEqualTo(0);
                }
                System.out.println( deleteFirst );
                assertThat(deleteFirst).isNull();
            }
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Timeout(5000)
    @Disabled("The checkReachability method is not totally reliable and can fall in an endless loop." +
            "We need to find a better way to check this.")
    public void testLeakAfterSessionDispose(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1655
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "end\n";

        ReleaseId releaseId = KieUtil.generateReleaseId("test");
        final List<Resource> resources = KieUtil.getResourcesFromDrls(drl);
        KieUtil.getKieModuleFromResources(releaseId, kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
        KieContainer kContainer = KieServices.Factory.get().newKieContainer(releaseId);
        KieBase kBase = KieBaseUtil.getDefaultKieBaseFromReleaseId(releaseId);
        KieSession ksession = kBase.newKieSession();

        ksession.insert( new Person("Mario", 40) );
        ksession.fireAllRules();

        checkReachability( ksession, Person.class::isInstance, true );
        checkReachability( kBase, ksession::equals, true );
        checkReachability( kContainer, ksession::equals, true );

        ksession.dispose();

        checkReachability( kContainer, Person.class::isInstance, false );
        checkReachability( kBase, ksession::equals, false );
        checkReachability( kContainer, ksession::equals, false );
    }

    private static void checkReachability( Object root, Predicate<Object> condition, boolean reachable ) {
        Collection<Object> results = checkObject(root, condition);
        assertThat(reachable ^ results.isEmpty()).isTrue();
    }

    private static Collection<Object> checkObject(final Object object, final Predicate<Object> condition) {
        Collection<Object> results = new ArrayList<>();
        final Set<Object> visited = Collections.newSetFromMap( new IdentityHashMap<>() );
        List<Object> childObjects = checkObject(object, condition, results, visited);
        while (childObjects != null && !childObjects.isEmpty()) {
            childObjects = checkObjects(childObjects, condition, results, visited);
        }
        return results;
    }

    private static List<Object> checkObjects(final List<Object> objects, final Predicate<Object> condition,
            final Collection<Object> results, final Set<Object> visited) {
        final List<Object> childObjects = new ArrayList<>();
        objects.forEach(object -> childObjects.addAll(checkObject(object, condition, results, visited)));
        return childObjects;
    }

    private static List<Object> checkObject(final Object object, final Predicate<Object> condition,
            final Collection<Object> results, final Set<Object> visited) {
        final List<Object> childObjects = new ArrayList<>();
        if (object != null) {
            if (!visited.add(object)) {
                return childObjects;
            }
            if (condition.test(object)) {
                results.add(object);
            } else {
                if (object instanceof Object[]) {
                    for (Object child: (Object[]) object) {
                        if (child != null) {
                            childObjects.addAll(getFieldsFromObject(child));
                        }
                    }
                } else if (!object.getClass().isArray()) {
                    childObjects.addAll(getFieldsFromObject(object));
                }
            }
        }
        return childObjects;
    }

    private static List<Object> getFieldsFromObject(final Object object) {
        final List<Object> childObjects = new ArrayList<>();
        for (Class c = object.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (Field field: c.getDeclaredFields()) {
                if ( Modifier.isStatic( field.getModifiers() )) {
                    continue;
                }
                if (field.getType().isPrimitive()) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    childObjects.add(field.get(object));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return childObjects;
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    @Timeout(60)
    public void testLeakWithMatchAndDelete(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R when\n" +
                        "    $p : Person(name == \"Mario\")\n" +
                        "then\n" +
                        "end\n";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        try (KieSession ksession = kBase.newKieSession()) {
            String text24kb = "A".repeat(24 * 1024);

            System.gc();
            long baseMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            for (int i = 0; i < 10000; i++) {
                Person person = new Person("Mario", i);
                person.setLikes(text24kb + i); // make sure that different String instances are created
                FactHandle factHandle = ksession.insert(person);
                int fired = ksession.fireAllRules();
                assertThat(fired).isEqualTo(1);

                ksession.delete(factHandle);
                ksession.fireAllRules();

                if (i % 1000 == 0) {
                    System.gc();
                    long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    System.out.println("Used memory: " + usedMemory);
                }
            }
            System.out.println("------------------");
            // Allow some memory for the processing overhead
            // The acceptableMemoryOverhead may not be a critical threshold. If the test fails, you may consider increasing it if it's not a memory leak.
            long acceptableMemoryOverhead = 10 * 1024 * 1024; // 10 MB
            System.gc();
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("Base memory: " + baseMemory);
            System.out.println("User memory: " + usedMemory);
            assertThat(usedMemory).isLessThan(baseMemory + acceptableMemoryOverhead);
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    @Timeout(60)
    public void testLeakWithJoinMatchAndDelete(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + Cheese.class.getCanonicalName() + "\n" +
                        "rule R when\n" +
                        "    $p : Person(name == \"Mario\")\n" +
                        "    $c : Cheese(type == \"stilton\")\n" +
                        "then\n" +
                        "end\n";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        try (KieSession ksession = kBase.newKieSession()) {
            String text24kb = "A".repeat(24 * 1024);

            System.gc();
            long baseMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            for (int i = 0; i < 10000; i++) {
                Person person = new Person("Mario", i);
                person.setLikes(text24kb + i); // make sure that different String instances are created
                FactHandle factHandlePerson = ksession.insert(person);

                Cheese cheese = new Cheese("stilton");
                FactHandle factHandleCheese = ksession.insert(cheese);

                int fired = ksession.fireAllRules();
                assertThat(fired).isEqualTo(1);

                ksession.delete(factHandlePerson);
                ksession.delete(factHandleCheese);
                ksession.fireAllRules();

                if (i % 1000 == 0) {
                    System.gc();
                    long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    System.out.println("Used memory: " + usedMemory);
                }
            }
            System.out.println("------------------");
            // Allow some memory for the processing overhead
            // The acceptableMemoryOverhead may not be a critical threshold. If the test fails, you may consider increasing it if it's not a memory leak.
            long acceptableMemoryOverhead = 10 * 1024 * 1024; // 10 MB
            System.gc();
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("Base memory: " + baseMemory);
            System.out.println("User memory: " + usedMemory);
            assertThat(usedMemory).isLessThan(baseMemory + acceptableMemoryOverhead);
        }
    }
}
