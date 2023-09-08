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
package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.base.base.ClassObjectType;
import org.drools.base.common.NetworkNode;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.DomainObject;
import org.drools.testcoverage.common.model.DomainObjectHolder;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Pet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FromTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FromTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    public static class ListsContainer {
        public List<String> getList1() {
            return Arrays.asList( "a", "bb", "ccc" );
        }
        public List<String> getList2() {
            return Arrays.asList( "1", "22", "333" );
        }
        public Number getSingleValue() {
            return 1;
        }
    }


    @Test
    public void testFromSharing() {
        testFromSharingCommon(kieBaseTestConfiguration, new HashMap<>(), 2, 2);
    }

    public static void testFromSharingCommon(KieBaseTestConfiguration kieBaseTestConfiguration,
                                             Map<String, String> configurationProperties,
                                             int expectedNumberOfFromNode,
                                             int numberOfSinksInSecondFromNode) {
        // Keeping original test as non-property reactive by default, just allowed.
        final String drl = fromSharingRule();

        final ReleaseId releaseId1 = KieServices.get().newReleaseId("org.kie", "from-test", "1");
        configurationProperties.put(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALLOWED.toString());

        final KieModule kieModule = KieUtil.getKieModuleFromDrls(releaseId1,
                                                                 kieBaseTestConfiguration,
                                                                 KieSessionTestConfiguration.STATEFUL_REALTIME,
                                                                 configurationProperties,
                                                                 drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase = kieContainer.getKieBase();

        final KieSession ksession = kbase.newKieSession();
        try {
            final ObjectTypeNode otn = insertObjectFireRules((InternalKnowledgeBase) kbase, ksession);

            // There is only 1 LIA
            assertThat(otn.getObjectSinkPropagator().size()).isEqualTo(1);
            final LeftInputAdapterNode lian = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];

            // There are only 2 FromNodes since R2 and R3 with the sharing the second From
            // There will be 3 FromNodes without the sharing, that is with exec model with plain lambda and native image
            final LeftTupleSink[] sinks = lian.getSinkPropagator().getSinks();
            assertThat(sinks.length).isEqualTo(expectedNumberOfFromNode);

            // The first from has R1 has sink
            assertThat(sinks[0].getSinkPropagator().size()).isEqualTo(1);

            // The second from has both R2 and R3 as sinks when node sharing
            // When node sharing is disabled, it will only have one
            assertThat(sinks[1].getSinkPropagator().size()).isEqualTo(numberOfSinksInSecondFromNode);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromSharingWithPropertyReactive() {
        // As above but with property reactive as default
        final String drl = fromSharingRule();
        // property reactive as default:
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ObjectTypeNode otn = insertObjectFireRules((InternalKnowledgeBase) kbase, ksession);

            // There are 2 LIAs, one for the list1 and the other for the list2
            assertThat(otn.getObjectSinkPropagator().size()).isEqualTo(2);
            final LeftInputAdapterNode lia0 = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];

            // There are only 2 FromNodes since R2 and R3 are sharing the second From

            // The first FROM node has R1 has sink
            final LeftTupleSink[] sinks0 = lia0.getSinkPropagator().getSinks();
            assertThat(sinks0.length).isEqualTo(1);
            assertThat(sinks0[0].getSinkPropagator().size()).isEqualTo(1);

            // The second FROM node has both R2 and R3 as sinks
            final LeftInputAdapterNode lia1 = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[1];
            final LeftTupleSink[] sinks1 = lia1.getSinkPropagator().getSinks();
            assertThat(sinks1.length).isEqualTo(1);
            assertThat(sinks1[0].getSinkPropagator().size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    public static String fromSharingRule() {
        return "import " + ListsContainer.class.getCanonicalName() + "\n" +
                "global java.util.List output1;\n" +
                "global java.util.List output2;\n" +
                "rule R1 when\n" +
                "    ListsContainer( $list : list1 )\n" +
                "    $s : String( length == 2 ) from $list\n" +
                "then\n" +
                "    output1.add($s);\n" +
                "end\n" +
                "rule R2 when\n" +
                "    ListsContainer( $list : list2 )\n" +
                "    $s : String( length == 2 ) from $list\n" +
                "then\n" +
                "    output2.add($s);\n" +
                "end\n" +
                "rule R3 when\n" +
                "    ListsContainer( $list : list2 )\n" +
                "    $s : String( length == 2 ) from $list\n" +
                "then\n" +
                "    output2.add($s);\n" +
                "end\n";
    }

    private static ObjectTypeNode insertObjectFireRules(InternalKnowledgeBase kbase, KieSession ksession) {
        final List<String> output1 = new ArrayList<>();
        ksession.setGlobal("output1", output1);
        final List<String> output2 = new ArrayList<>();
        ksession.setGlobal("output2", output2);

        ksession.insert(new ListsContainer());
        ksession.fireAllRules();

        assertThat(output1.get(0)).isEqualTo("bb");
        assertThat(output2.get(0)).isEqualTo("22");
        assertThat(output2.get(1)).isEqualTo("22");

        final EntryPointNode epn = kbase.getRete().getEntryPointNodes().values().iterator().next();
        return epn.getObjectTypeNodes().get(new ClassObjectType(ListsContainer.class));
    }

    @Test
    public void testFromSharingWithAccumulate() {
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "import java.util.ArrayList;\n" +
                        "import " + Cheesery.class.getCanonicalName() + " ;\n" +
                        "import " + Cheese.class.getCanonicalName() + " ;\n" +
                        "\n" +
                        "global java.util.List output1;\n" +
                        "global java.util.List output2;\n" +
                        "\n" +
                        "rule R1\n" +
                        "    when\n" +
                        "        $cheesery : Cheesery()\n" +
                        "        $list     : List( ) from accumulate( $cheese : Cheese( ) from $cheesery.getCheeses(),\n" +
                        "                                             init( List l = new ArrayList(); ),\n" +
                        "                                             action( l.add( $cheese ); )\n" +
                        "                                             result( l ) )\n" +
                        "    then\n" +
                        "        output1.add( $list );\n" +
                        "end\n" +
                        "rule R2\n" +
                        "    when\n" +
                        "        $cheesery : Cheesery()\n" +
                        "        $list     : List( ) from accumulate( $cheese : Cheese( ) from $cheesery.getCheeses(),\n" +
                        "                                             init( List l = new ArrayList(); ),\n" +
                        "                                             action( l.add( $cheese ); )\n" +
                        "                                             result( l ) )\n" +
                        "    then\n" +
                        "        output2.add( $list );\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);

        EntryPointNode epn = ((InternalKnowledgeBase) kbase).getRete().getEntryPointNode(EntryPointId.DEFAULT);
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( Cheesery.class) );
        NetworkNode[] otnSinks = otn.getSinks();
        assertThat(otnSinks.length).isEqualTo(1);
        LeftInputAdapterNode lia = (LeftInputAdapterNode) otnSinks[0];
        NetworkNode[] liaSinks = lia.getSinks();
        // there must be only 1 shared from node
        assertThat(Stream.of(liaSinks).filter(sink -> sink instanceof FromNode).count()).isEqualTo(1);

        final KieSession ksession = kbase.newKieSession();
        try {
            final List<?> output1 = new ArrayList<>();
            ksession.setGlobal( "output1", output1 );
            final List<?> output2 = new ArrayList<>();
            ksession.setGlobal( "output2", output2 );

            final Cheesery cheesery = new Cheesery();
            cheesery.addCheese( new Cheese( "stilton", 8 ) );
            cheesery.addCheese( new Cheese("provolone", 8 ) );

            final FactHandle cheeseryHandle = ksession.insert(cheesery );

            ksession.fireAllRules();
            assertThat(output1.size()).isEqualTo(1);
            assertThat(((List) output1.get(0)).size()).isEqualTo(2);
            assertThat(output2.size()).isEqualTo(1);
            assertThat(((List) output2.get(0)).size()).isEqualTo(2);

            output1.clear();
            output2.clear();

            ksession.update( cheeseryHandle, cheesery );
            ksession.fireAllRules();

            assertThat(output1.size()).isEqualTo(1);
            assertThat(((List) output1.get(0)).size()).isEqualTo(2);
            assertThat(output2.size()).isEqualTo(1);
            assertThat(((List) output2.get(0)).size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromWithSingleValue() {
        // DROOLS-1243
        final String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $list : ListsContainer( )\n" +
                        "    $s : Integer() from $list.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> out = new ArrayList<>();
            ksession.setGlobal( "out", out );

            ksession.insert( new ListsContainer() );
            ksession.fireAllRules();

            assertThat(out.size()).isEqualTo(1);
            assertThat((int) out.get(0)).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromWithSingleValueAndIncompatibleType() {
        // DROOLS-1243
        final String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $list : ListsContainer( )\n" +
                        "    $s : String() from $list.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    public static class Container2 {
        private final Number wrapped;
        public Container2(final Number wrapped) {
            this.wrapped = wrapped;
        }
        public Number getSingleValue() {
            return this.wrapped;
        }
    }
    @Test
    public void testFromWithInterfaceAndAbstractClass() {
        final String drl =
                "import " + Container2.class.getCanonicalName() + "\n" +
                        "import " + Comparable.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c2 : Container2( )\n" +
                        "    $s : Comparable() from $c2.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> out = new ArrayList<>();
            ksession.setGlobal( "out", out );

            ksession.insert( new Container2(1) );
            ksession.fireAllRules();

            assertThat(out.size()).isEqualTo(1);
            assertThat((int) out.get(0)).isEqualTo(1);

            out.clear();

            ksession.insert( new Container2( new AtomicInteger(1) ) );
            ksession.fireAllRules();

            assertThat(out.size()).isEqualTo(0);
        } finally {
            ksession.dispose();
        }
    }

    public static class Container2b {
        private final AtomicInteger wrapped;
        public Container2b(final AtomicInteger wrapped) {
            this.wrapped = wrapped;
        }
        public AtomicInteger getSingleValue() {
            return this.wrapped;
        }
    }
    public interface CustomIntegerMarker {}
    public static class CustomInteger extends AtomicInteger implements CustomIntegerMarker {
        public CustomInteger(final int initialValue) {
            super(initialValue);
        }
    }
    @Test
    public void testFromWithInterfaceAndConcreteClass() {
        final String drl =
                "import " + Container2b.class.getCanonicalName() + "\n" +
                        "import " + CustomIntegerMarker.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c2 : Container2b( )\n" +
                        "    $s : CustomIntegerMarker() from $c2.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<AtomicInteger> out = new ArrayList<>();
            ksession.setGlobal( "out", out );

            ksession.insert( new Container2b( new CustomInteger(1) ) );
            ksession.fireAllRules();

            assertThat(out.size()).isEqualTo(1);
            assertThat(out.get(0).get()).isEqualTo(1);

            out.clear();

            ksession.insert( new Container2b( new AtomicInteger(1) ) );
            ksession.fireAllRules();

            assertThat(out.size()).isEqualTo(0);
        } finally {
            ksession.dispose();
        }
    }

    public static class Container3 {
        private final Integer wrapped;
        public Container3(final Integer wrapped) {
            this.wrapped = wrapped;
        }
        public Integer getSingleValue() {
            return this.wrapped;
        }
    }

    @Test
    public void testFromWithInterfaceAndFinalClass() {
        final String drl =
                "import " + Container3.class.getCanonicalName() + "\n" +
                        "import " + CustomIntegerMarker.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c3 : Container3( )\n" +
                        "    $s : CustomIntegerMarker() from $c3.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testBasicFrom() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "import java.util.List;\n" +
                "\n" +
                "global List list1;\n" +
                "global List list2;\n" +
                "global List list3;\n" +
                "global Cheesery cheesery;\n" +
                "\n" +
                "rule \"test from using a global\"\n" +
                "    when\n" +
                "        $cheese : Cheese() from cheesery.getCheeses()\n" +
                "    then\n" +
                "        list1.add( $cheese );\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"test from using a declaration\"\n" +
                "    when\n" +
                "        $ch : Cheesery()\n" +
                "        $cheese : Cheese() from $ch.getCheeses()\n" +
                "    then\n" +
                "        list2.add( $cheese );\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"test from with filter\"\n" +
                "    when\n" +
                "        $cheese : Cheese(type == \"stilton\" ) from cheesery.getCheeses()\n" +
                "    then\n" +
                "        list3.add( $cheese );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list1 = new ArrayList();
            ksession.setGlobal("list1", list1);
            final List list2 = new ArrayList();
            ksession.setGlobal("list2", list2);
            final List list3 = new ArrayList();
            ksession.setGlobal("list3", list3);

            final Cheesery cheesery = new Cheesery();
            final Cheese stilton = new Cheese("stilton", 12);
            final Cheese cheddar = new Cheese("cheddar", 15);
            cheesery.addCheese(stilton);
            cheesery.addCheese(cheddar);
            ksession.setGlobal("cheesery", cheesery);
            ksession.insert(cheesery);

            final Person p = new Person("stilton");
            ksession.insert(p);

            ksession.fireAllRules();
            ksession.fireAllRules();

            // from using a global
            assertThat(((List) ksession.getGlobal("list1")).size()).isEqualTo(2);
            assertThat(((List) ksession.getGlobal("list1")).get(0)).isEqualTo(cheddar);
            assertThat(((List) ksession.getGlobal("list1")).get(1)).isEqualTo(stilton);

            // from using a declaration
            assertThat(((List) ksession.getGlobal("list2")).size()).isEqualTo(2);
            assertThat(((List) ksession.getGlobal("list2")).get(0)).isEqualTo(cheddar);
            assertThat(((List) ksession.getGlobal("list2")).get(1)).isEqualTo(stilton);

            // from using a declaration
            assertThat(((List) ksession.getGlobal("list3")).size()).isEqualTo(1);
            assertThat(((List) ksession.getGlobal("list3")).get(0)).isEqualTo(stilton);
        } finally {
            ksession.dispose();
        }
    }

    public static class ToListFunction {

        public List toList(final Object object1,
                           final Object object2,
                           final String object3,
                           final int integer,
                           final Map map,
                           final List inputList) {
            final List<Object> list = new ArrayList<>();
            list.add(object1);
            list.add(object2);
            list.add(object3);
            list.add(integer);
            list.add(map);
            list.add(inputList);
            return list;
        }
    }

    @Test @Ignore
    public void testFromWithParams() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                " \n" +
                "import " + ToListFunction.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "global ToListFunction testObject;\n" +
                "global java.util.List list;\n" +
                "global java.lang.Object globalObject;\n" +
                "\n" +
                "rule \"test from\"\n" +
                "    when\n" +
                "        $person : Person()\n" +
                "        $object : Object() from testObject.toList(globalObject, $person, \"literal\", 42, [ $person : globalObject, \"key1\" : [ \"key2\" : \"value2\"]], [$person, 42, [\"x\", \"y\"]])\n" +
                "    then\n" +
                "        list.add( $object );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            final Object globalObject = new Object();
            ksession.setGlobal("list", list);
            ksession.setGlobal("testObject", new ToListFunction());
            ksession.setGlobal("globalObject", globalObject);

            final Person bob = new Person("bob");
            ksession.insert(bob);

            ksession.fireAllRules();

            assertThat(((List) ksession.getGlobal("list")).size()).isEqualTo(6);

            final List array = (List) ((List) ksession.getGlobal("list")).get(0);
            assertThat(array.size()).isEqualTo(3);
            final Person p = (Person) array.get(0);
            assertThat(bob).isEqualTo(p);

            assertThat(array.get(1)).isEqualTo(42);

            final List nested = (List) array.get(2);
            assertThat(nested.get(0)).isEqualTo("x");
            assertThat(nested.get(1)).isEqualTo("y");

            final Map map = (Map) ((List) ksession.getGlobal("list")).get(1);
            assertThat(map.keySet().size()).isEqualTo(2);

            assertThat(map.containsKey(bob)).isTrue();
            assertThat(map.get(bob)).isEqualTo(globalObject);

            assertThat(map.containsKey("key1")).isTrue();
            final Map nestedMap = (Map) map.get("key1");
            assertThat(nestedMap.keySet().size()).isEqualTo(1);
            assertThat(nestedMap.containsKey("key2")).isTrue();
            assertThat(nestedMap.get("key2")).isEqualTo("value2");

            assertThat(((List) ksession.getGlobal("list")).get(2)).isEqualTo(42);
            assertThat(((List) ksession.getGlobal("list")).get(3)).isEqualTo("literal");
            assertThat(((List) ksession.getGlobal("list")).get(4)).isEqualTo(bob);
            assertThat(((List) ksession.getGlobal("list")).get(5)).isEqualTo(globalObject);
        } finally {
            ksession.dispose();
        }
    }

    public static class Results {
        public int getResultsCount() {
            return 1;
        }
    }

    public static class Storage {
        public Results search(Query query) {
            return new Results();
        }
    }

    public static class Query {
        public Query(String pattern, String column) {

        }
    }

    @Test
    public void testFromWithNewConstructor() {

        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "\n" +
                "import " + Query.class.getCanonicalName() + ";\n" +
                "import " + Storage.class.getCanonicalName() + ";\n" +
                "import " + Results.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"Verify_1\"\n" +
                "    when\n" +
                "        content : Storage()\n" +
                "        results : Results( ) from content.search(new Query(\"test\",\"field\"))\n" +
                "    then\n" +
                "        System.out.println( results );\n" +
                "end";

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("from-test", kieBaseTestConfiguration, drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBaseConfiguration kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setProperty(LanguageLevelOption.PROPERTY_NAME, "DRL5");
        kieContainer.newKieBase(kieBaseConfiguration);
    }

    /**
     * JBRULES-1415 Certain uses of from causes NullPointerException in WorkingMemoryLogger
     */
    @Test
    public void testFromDeclarationWithWorkingMemoryLogger() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
            "import " + Cheesery.class.getCanonicalName() + ";\n" +
            "import " + Cheese.class.getCanonicalName() + ";\n" +
            "global java.util.List list\n" +
            "rule \"Test Rule\"\n" +
            "when\n" +
            "    $cheesery : Cheesery()\n" +
            "    Cheese( $type : type) from $cheesery.cheeses\n" +
            "then\n" +
            "    list.add( $type );\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Cheesery cheesery = new Cheesery();
            cheesery.addCheese(new Cheese("stilton", 22));

            session.insert(cheesery);

            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).size()).isEqualTo(1);
            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo("stilton");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testFromArrayIteration() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + DomainObject.class.getCanonicalName() + ";\n" +
                "import " + DomainObjectHolder.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "    $holder : DomainObjectHolder()\n" +
                "    $object : DomainObject( $message : message) from $holder.objects;\n" +
                "then\n" +
                "    list.add( $message );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);
            session.insert(new DomainObjectHolder());

            session.fireAllRules();

            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0)).isEqualTo("Message3");
            assertThat(list.get(1)).isEqualTo("Message2");
            assertThat(list.get(2)).isEqualTo("Message1");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testFromExprFollowedByNot() {
        final String drl =
                    "package org.drools.compiler.integrationtests.operators;\n" +
                    "import " + Person.class.getCanonicalName() + ";\n" +
                    "import " + Pet.class.getCanonicalName() + ";\n" +
                    "global java.util.List list;\n" +
                    "rule \"Rule 1\"\n" +
                    "    when\n" +
                    "        p : Person ($var: pet )\n" +
                    "        Pet () from $var\n" +
                    "        not Pet ()\n" +
                    "    then\n" +
                    "       list.add( p );\n" +
                    "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person p = new Person();
            p.setPet(new Pet(Pet.PetType.PARROT));
            ksession.insert(p);
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isSameAs(p);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromNestedAccessors() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Order.class.getCanonicalName() + ";\n" +
                "import " + OrderItem.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"test from nested accessors\"\n" +
                "when\n" +
                "    $oi : OrderItem( seq == 1 )\n" +
                "    $os : Order.OrderStatus() from $oi.order.status\n" +
                "then\n" +
                "    results.add( $os );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final Order order1 = new Order(11, "Bob");
            final OrderItem item11 = new OrderItem(order1, 1);
            final OrderItem item12 = new OrderItem(order1, 2);
            order1.addItem(item11);
            order1.addItem(item12);

            ksession.insert(order1);
            ksession.insert(item11);
            ksession.insert(item12);

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isSameAs(order1.getStatus());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromNodeWithMultipleBetas() {
        final String drl = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "   $p : Person( $name : name, $addresses : addresses )\n" +
                "   $c : Cheese( $type: type == $name )\n" +
                "   $a : Address( street == $type, city == $name ) from $addresses\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person p = new Person("x");
            p.addAddress(new Address("x", 1, "x"));
            p.addAddress(new Address("y", 2, "y"));
            ksession.insert(p);

            ksession.insert(new Cheese("x"));
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFromWithStrictModeOn() {
        // JBRULES-3533
        final String drl =
                "import java.util.Map;\n" +
                        "dialect \"mvel\"\n" +
                        "rule \"LowerCaseFrom\"\n" +
                        "when\n" +
                        "   Map($valOne : this['keyOne'] !=null)\n" +
                        "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                        "then\n" +
                        "   System.out.println( $valOne.toLowerCase() );\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testJavaImplicitWithFrom() {
        testDialectWithFrom("java");
    }

    @Test
    public void testMVELImplicitWithFrom() {
        testDialectWithFrom("mvel");
    }

    private void testDialectWithFrom(final String dialect) {
        final String drl = "" +
                "package org.drools.compiler.test \n" +
                "import java.util.List \n" +
                "global java.util.List list \n" +
                "global java.util.List list2 \n" +
                "rule \"show\" dialect \"" + dialect + "\" \n" +
                "when  \n" +
                "    $m : List( eval( size == 0 ) ) from [list] \n" +
                "then \n" +
                "    list2.add('r1'); \n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.setGlobal("list2", list);

            ksession.fireAllRules();
            assertThat(list.get(0)).isEqualTo("r1");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMultipleFroms() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import java.util.List;\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global Cheesery cheesery;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule MyRule\n" +
                "    dialect \"java\"\n" +
                "when\n" +
                "    $i : List() from collect(Cheese() from cheesery.getCheeses())\n" +
                "    $k : List() from collect(Cheese() from cheesery.getCheeses())\n" +
                "then\n" +
                "    results.add( $i );\n" +
                "    results.add( $k );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Cheesery cheesery = new Cheesery();
            cheesery.addCheese(new Cheese("stilton", 15));
            cheesery.addCheese(new Cheese("brie", 10));

            ksession.setGlobal("cheesery", cheesery);

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(2);
            assertThat(((List) results.get(0)).size()).isEqualTo(2);
            assertThat(((List) results.get(1)).size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNetworkBuildErrorAcrossEntryPointsAndFroms() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "rule rule1\n" +
                "when\n" +
                "         Cheese() from entry-point \"testep\"\n" +
                "    $p : Person() from list\n" +
                "then \n" +
                "  list.add( \"rule1\" ) ;\n" +
                "  insert( $p );\n" +
                "end\n" +
                "rule rule2\n" +
                "when\n" +
                "  $p : Person() \n" +
                "then \n" +
                "  list.add( \"rule2\" ) ;\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final EntryPoint ep = ksession.getEntryPoint("testep");

            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            list.add(new Person("darth"));
            ep.insert(new Cheese("cheddar"));

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(3);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testUpdateFromCollect() {
        // DROOLS-6504
        final String drl =
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + ClassWithValues.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "      $cwvs: List(size > 0) from collect (ClassWithValues(values.size == 0))\n" +
                "      $values: List(size > 0) from collect (String())\n" +
                "    then\n" +
                "      \n" +
                "      for (ClassWithValues cwv: (List<ClassWithValues>)$cwvs) {\n" +
                "        cwv.add(\"not in memory\");\n" +
                "        ((List<String>)$values).forEach(cwv::add);\n" +
                "        update(cwv);\n" +
                "      }\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        ksession.insert(new ClassWithValues());
        ksession.insert("test");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class ClassWithValues {
        private List<String> values = new ArrayList<>();

        public void add(String value) {
            this.values.add(value);
        }

        public List<String> getValues() {
            return this.values;
        }
    }

    private final String RULE_HEAD = "package org.drools.compiler.integrationtests.operators;\n" +
                                     "import " + Person.class.getCanonicalName() + ";\n" +
                                     "global java.util.List list\n";

    private final String RULE_WITH_JOIN = "rule R1\n" +
                                          "agenda-group \"group2\"\n" +
                                          "no-loop true\n" +
                                          "    when\n" +
                                          "        $p1 : Person ( )\n" +
                                          "        $s : String(this == \"ABC\")\n" +
                                          "    then\n" +
                                          "end\n";

    private final String RULE_WITH_FROM = "rule R2\n" +
                                          "salience -70\n" +
                                          "agenda-group \"group1\"\n" +
                                          "no-loop true\n" + // no-loop is required for eager activation creation
                                          "    when\n" +
                                          "        $p1 : Person ( )\n" +
                                          "        $p2 : Person ( age < 2 ) from $p1\n" + // meaningless use of "from" but valid syntax
                                          "    then\n" +
                                          "       list.add( $p2.getName() );\n" +
                                          "end\n" +
                                          "rule R3\n" +
                                          "agenda-group \"group1\"\n" +
                                          "salience -30\n" +
                                          "no-loop true\n" +
                                          "    when\n" +
                                          "        $p : Person ( age == 1 )\n" +
                                          "    then\n" +
                                          "       modify($p){setAge(10)};\n" +
                                          "end\n";

    @Test
    public void testJoinAndFrom() {
        // DROOLS-6821
        // Add JoinNode first
        runKSessionWithAgendaGroup(RULE_HEAD +
                                   RULE_WITH_JOIN +
                                   RULE_WITH_FROM);
    }

    @Test
    public void testFromAndJoin() {
        // DROOLS-6821
        // Add FromNode first
        runKSessionWithAgendaGroup(RULE_HEAD +
                                   RULE_WITH_FROM +
                                   RULE_WITH_JOIN);
    }

    private void runKSessionWithAgendaGroup(String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);

        final KieSession ksession = kbase.newKieSession();
        ksession.addEventListener(new DebugAgendaEventListener());
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p = new Person("John", 1);
            ksession.insert(p);
            ksession.fireAllRules();
            ksession.getAgenda().getAgendaGroup("group1").setFocus();
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(0); // R2 should not be fired
        } finally {
            ksession.dispose();
        }
    }
}
