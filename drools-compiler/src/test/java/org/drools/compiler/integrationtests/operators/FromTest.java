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

package org.drools.compiler.integrationtests.operators;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.DomainObjectHolder;
import org.drools.compiler.FromTestClass;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.Pet;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteDumper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

public class FromTest extends CommonTestMethodBase {

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
        // Keeping original test as non-property reactive by default, just allowed.
        String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
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

        KieBase kbase = new KieHelper(PropertySpecificOption.ALLOWED).addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        ReteDumper.dumpRete(kbase);

        List<String> output1 = new ArrayList<String>();
        ksession.setGlobal( "output1", output1 );
        List<String> output2 = new ArrayList<String>();
        ksession.setGlobal( "output2", output2 );

        FactHandle fh = ksession.insert( new ListsContainer() );
        ksession.fireAllRules();

        assertEquals("bb", output1.get( 0 ));
        assertEquals("22", output2.get( 0 ));
        assertEquals("22", output2.get( 1 ));

        EntryPointNode epn = ( (InternalKnowledgeBase)kbase ).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( ListsContainer.class ) );

        // There is only 1 LIA
        assertEquals( 1, otn.getObjectSinkPropagator().size() );
        LeftInputAdapterNode lian = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];

        // There are only 2 FromNodes since R2 and R3 are sharing the second From
        LeftTupleSink[] sinks = lian.getSinkPropagator().getSinks();
        assertEquals( 2, sinks.length );

        // The first from has R1 has sink
        assertEquals( 1, sinks[0].getSinkPropagator().size() );

        // The second from has both R2 and R3 as sinks
        assertEquals( 2, sinks[1].getSinkPropagator().size() );
    }

    @Test
    public void testFromSharingWithPropertyReactive() {
        // As above but with property reactive as default
        String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
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
        // property reactive as default:
        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        ReteDumper.dumpRete(kbase);

        List<String> output1 = new ArrayList<String>();
        ksession.setGlobal( "output1", output1 );
        List<String> output2 = new ArrayList<String>();
        ksession.setGlobal( "output2", output2 );

        FactHandle fh = ksession.insert( new ListsContainer() );
        ksession.fireAllRules();

        assertEquals("bb", output1.get( 0 ));
        assertEquals("22", output2.get( 0 ));
        assertEquals("22", output2.get( 1 ));

        EntryPointNode epn = ( (InternalKnowledgeBase)kbase ).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( ListsContainer.class ) );

        // There are 2 LIAs, one for the list1 and the other for the list2
        assertEquals( 2, otn.getObjectSinkPropagator().size() );
        LeftInputAdapterNode lia0 = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];

        // There are only 2 FromNodes since R2 and R3 are sharing the second From

        // The first FROM node has R1 has sink
        LeftTupleSink[] sinks0 = lia0.getSinkPropagator().getSinks();
        assertEquals( 1, sinks0.length );
        assertEquals( 1, sinks0[0].getSinkPropagator().size() );

        // The second FROM node has both R2 and R3 as sinks
        LeftInputAdapterNode lia1 = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[1];
        LeftTupleSink[] sinks1 = lia1.getSinkPropagator().getSinks();
        assertEquals( 1, sinks1.length );
        assertEquals( 2, sinks1[0].getSinkPropagator().size() );
    }

    @Test
    public void testFromSharingWithAccumulate() {
        String drl =
                "package org.drools.compiler\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "import java.util.ArrayList;\n" +
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

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<?> output1 = new ArrayList<Object>();
        ksession.setGlobal( "output1", output1 );
        List<?> output2 = new ArrayList<Object>();
        ksession.setGlobal( "output2", output2 );

        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "stilton", 8 ) );
        cheesery.addCheese( new Cheese( "provolone", 8 ) );

        FactHandle cheeseryHandle = ksession.insert( cheesery );

        ksession.fireAllRules();
        assertEquals( 1, output1.size() );
        assertEquals( 2, ( (List) output1.get( 0 ) ).size() );
        assertEquals( 1, output2.size() );
        assertEquals( 2, ( (List) output2.get( 0 ) ).size() );

        output1.clear();
        output2.clear();

        ksession.update( cheeseryHandle, cheesery );
        ksession.fireAllRules();

        assertEquals( 1, output1.size() );
        assertEquals( 2, ( (List) output1.get( 0 ) ).size() );
        assertEquals( 1, output2.size() );
        assertEquals( 2, ( (List) output2.get( 0 ) ).size() );
    }

    @Test
    public void testFromWithSingleValue() {
        // DROOLS-1243
        String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $list : ListsContainer( )\n" +
                        "    $s : Integer() from $list.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<Integer> out = new ArrayList<Integer>();
        ksession.setGlobal( "out", out );

        ksession.insert( new ListsContainer() );
        ksession.fireAllRules();

        assertEquals( 1, out.size() );
        assertEquals( 1, (int)out.get(0) );
    }

    @Test
    public void testFromWithSingleValueAndIncompatibleType() {
        // DROOLS-1243
        String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $list : ListsContainer( )\n" +
                        "    $s : String() from $list.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    public static class Container2 {
        private Number wrapped;
        public Container2(Number wrapped) {
            this.wrapped = wrapped;
        }
        public Number getSingleValue() {
            return this.wrapped;
        }
    }
    @Test
    public void testFromWithInterfaceAndAbstractClass() {
        String drl =
                "import " + Container2.class.getCanonicalName() + "\n" +
                        "import " + Comparable.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c2 : Container2( )\n" +
                        "    $s : Comparable() from $c2.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<Integer> out = new ArrayList<Integer>();
        ksession.setGlobal( "out", out );

        ksession.insert( new Container2( new Integer(1) ) );
        ksession.fireAllRules();

        assertEquals( 1, out.size() );
        assertEquals( 1, (int)out.get(0) );


        out.clear();

        ksession.insert( new Container2( new AtomicInteger(1) ) );
        ksession.fireAllRules();

        assertEquals( 0, out.size() );
    }

    public static class Container2b {
        private AtomicInteger wrapped;
        public Container2b(AtomicInteger wrapped) {
            this.wrapped = wrapped;
        }
        public AtomicInteger getSingleValue() {
            return this.wrapped;
        }
    }
    public static interface CustomIntegerMarker {}
    public static class CustomInteger extends AtomicInteger implements CustomIntegerMarker {
        public CustomInteger(int initialValue) {
            super(initialValue);
        }
    }
    @Test
    public void testFromWithInterfaceAndConcreteClass() {
        String drl =
                "import " + Container2b.class.getCanonicalName() + "\n" +
                        "import " + CustomIntegerMarker.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c2 : Container2b( )\n" +
                        "    $s : CustomIntegerMarker() from $c2.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<AtomicInteger> out = new ArrayList<>();
        ksession.setGlobal( "out", out );

        ksession.insert( new Container2b( new CustomInteger(1) ) );
        ksession.fireAllRules();

        assertEquals( 1, out.size() );
        assertEquals( 1, ((CustomInteger)out.get(0)).get() );


        out.clear();

        ksession.insert( new Container2b( new AtomicInteger(1) ) );
        ksession.fireAllRules();

        assertEquals( 0, out.size() );
    }

    public static class Container3 {
        private Integer wrapped;
        public Container3(Integer wrapped) {
            this.wrapped = wrapped;
        }
        public Integer getSingleValue() {
            return this.wrapped;
        }
    }

    @Test
    public void testFromWithInterfaceAndFinalClass() {
        String drl =
                "import " + Container3.class.getCanonicalName() + "\n" +
                        "import " + CustomIntegerMarker.class.getCanonicalName() + "\n" +
                        "global java.util.List out;\n" +
                        "rule R1 when\n" +
                        "    $c3 : Container3( )\n" +
                        "    $s : CustomIntegerMarker() from $c3.singleValue\n" +
                        "then\n" +
                        "    out.add($s);\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();

        // Integer is final class, so there cannot be ever the case of pattern matching in the `from` on a non-extended interface to ever match.
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testBasicFrom() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_From.drl");
        kbase = SerializationHelper.serializeObject(kbase);

        KieSession ksession = createKnowledgeSession(kbase);
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

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        // from using a global
        assertEquals(2, ((List) ksession.getGlobal("list1")).size());
        assertEquals(cheddar, ((List) ksession.getGlobal("list1")).get(0));
        assertEquals(stilton, ((List) ksession.getGlobal("list1")).get(1));

        // from using a declaration
        assertEquals(2, ((List) ksession.getGlobal("list2")).size());
        assertEquals(cheddar, ((List) ksession.getGlobal("list2")).get(0));
        assertEquals(stilton, ((List) ksession.getGlobal("list2")).get(1));

        // from using a declaration
        assertEquals(1, ((List) ksession.getGlobal("list3")).size());
        assertEquals(stilton, ((List) ksession.getGlobal("list3")).get(0));
    }

    @Test
    public void testFromWithParams() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_FromWithParams.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        final Object globalObject = new Object();
        ksession.setGlobal("list", list);
        ksession.setGlobal("testObject", new FromTestClass());
        ksession.setGlobal("globalObject", globalObject);

        final Person bob = new Person("bob");
        ksession.insert(bob);

        ksession.fireAllRules();

        assertEquals(6, ((List) ksession.getGlobal("list")).size());

        final List array = (List) ((List) ksession.getGlobal("list")).get(0);
        assertEquals(3, array.size());
        final Person p = (Person) array.get(0);
        assertEquals(p, bob);

        assertEquals(42, array.get(1));

        final List nested = (List) array.get(2);
        assertEquals("x", nested.get(0));
        assertEquals("y", nested.get(1));

        final Map map = (Map) ((List) ksession.getGlobal("list")).get(1);
        assertEquals(2, map.keySet().size());

        assertTrue(map.keySet().contains(bob));
        assertEquals(globalObject, map.get(bob));

        assertTrue(map.keySet().contains("key1"));
        final Map nestedMap = (Map) map.get("key1");
        assertEquals(1, nestedMap.keySet().size());
        assertTrue(nestedMap.keySet().contains("key2"));
        assertEquals("value2", nestedMap.get("key2"));

        assertEquals(42, ((List) ksession.getGlobal("list")).get(2));
        assertEquals("literal", ((List) ksession.getGlobal("list")).get(3));
        assertEquals(bob, ((List) ksession.getGlobal("list")).get(4));
        assertEquals(globalObject, ((List) ksession.getGlobal("list")).get(5));
    }

    @Test
    public void testFromWithNewConstructor() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr descr = parser.parse(new InputStreamReader(getClass().getResourceAsStream("test_FromWithNewConstructor.drl")));

        final Collection<KnowledgePackage> pkgs = loadKnowledgePackages(descr);
        SerializationHelper.serializeObject(pkgs);
    }

    /**
     * JBRULES-1415 Certain uses of from causes NullPointerException in WorkingMemoryLogger
     */
    @Test
    public void testFromDeclarationWithWorkingMemoryLogger() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Cheesery\n";
        rule += "import org.drools.compiler.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $cheesery : Cheesery()\n";
        rule += "    Cheese( $type : type) from $cheesery.cheeses\n";
        rule += "then\n";
        rule += "    list.add( $type );\n";
        rule += "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(new Cheese("stilton", 22));

        session.insert(cheesery);

        session.fireAllRules();

        assertEquals(1, ((List) session.getGlobal("list")).size());
        assertEquals("stilton", ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testFromArrayIteration() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FromArrayIteration.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();

        session.setGlobal("list", list);
        session.insert(new DomainObjectHolder());

        session.fireAllRules();

        assertEquals(3, list.size());

        assertEquals("Message3", list.get(0));
        assertEquals("Message2", list.get(1));
        assertEquals("Message1", list.get(2));
    }

    @Test
    public void testFromExprFollowedByNot() {
        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "global java.util.List list;\n";
        rule += "rule \"Rule 1\"\n";
        rule += "    when\n";
        rule += "        p : Person ($var: pet )\n";
        rule += "        Pet () from $var\n";
        rule += "        not Pet ()\n";
        rule += "    then\n";
        rule += "       list.add( p );\n";
        rule += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person();
        p.setPet(new Pet());
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertSame(p, list.get(0));
    }

    @Test
    public void testFromNestedAccessors() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FromNestedAccessors.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

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
        assertEquals(1, list.size());
        assertSame(order1.getStatus(), list.get(0));
    }

    @Test
    public void testFromNodeWithMultipleBetas() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( $name : name, $addresses : addresses )\n" +
                "   $c : Cheese( $type: type == $name )\n" +
                "   $a : Address( street == $type, suburb == $name ) from $addresses\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person("x");
        p.addAddress(new Address("x", "x", "x"));
        p.addAddress(new Address("y", "y", "y"));
        ksession.insert(p);

        ksession.insert(new Cheese("x"));
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testFromWithStrictModeOff() {
        // JBRULES-3533
        final String str =
                "import java.util.Map;\n" +
                        "dialect \"mvel\"\n" +
                        "rule \"LowerCaseFrom\"\n" +
                        "when\n" +
                        "   Map($valOne : this['keyOne'] !=null)\n" +
                        "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                        "then\n" +
                        "   System.out.println( $valOne.toLowerCase() );\n" +
                        "end\n";

        final KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
        final MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration( "mvel" );
        mvelConf.setStrict( false );
        mvelConf.setLangLevel( 5 );

        final KnowledgeBase kbase = loadKnowledgeBaseFromString( pkgBuilderCfg, str );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final Map<String, String> testMap = new HashMap<String, String>();
        testMap.put( "keyOne", "valone" );
        testMap.put( "valTwo", "valTwo" );
        ksession.insert( testMap );
        assertEquals( 1, ksession.fireAllRules() );
        ksession.dispose();
    }

    @Test
    public void testFromWithStrictModeOn() {
        // JBRULES-3533
        final String str =
                "import java.util.Map;\n" +
                        "dialect \"mvel\"\n" +
                        "rule \"LowerCaseFrom\"\n" +
                        "when\n" +
                        "   Map($valOne : this['keyOne'] !=null)\n" +
                        "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                        "then\n" +
                        "   System.out.println( $valOne.toLowerCase() );\n" +
                        "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testJavaImplicitWithFrom() throws IOException, ClassNotFoundException {
        testDialectWithFrom("java");
    }

    @Test
    public void testMVELImplicitWithFrom() throws IOException, ClassNotFoundException {
        testDialectWithFrom("mvel");
    }

    private void testDialectWithFrom(final String dialect) throws IOException, ClassNotFoundException {
        final String str = "" +
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

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.setGlobal("list2", list);

        ksession.fireAllRules();
        assertEquals("r1", list.get(0));
    }

    @Test
    public void testMultipleFroms() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_multipleFroms.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(new Cheese("stilton", 15));
        cheesery.addCheese(new Cheese("brie", 10));

        ksession.setGlobal("cheesery", cheesery);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, ((List) results.get(0)).size());
        assertEquals(2, ((List) results.get(1)).size());
    }

    @Test
    public void testNetworkBuildErrorAcrossEntryPointsAndFroms() throws Exception {
        String rule1 = "package org.drools.compiler\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "         Cheese() from entry-point \"testep\"\n";
        rule1 += "    $p : Person() from list\n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule1\" ) ;\n";
        rule1 += "  insert( $p );\n";
        rule1 += "end\n";
        rule1 += "rule rule2\n";
        rule1 += "when\n";
        rule1 += "  $p : Person() \n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule2\" ) ;\n";
        rule1 += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule1);
        final KieSession ksession = createKnowledgeSession(kbase);
        final EntryPoint ep = ksession.getEntryPoint("testep");

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        list.add(new Person("darth"));
        ep.insert(new Cheese("cheddar"));

        ksession.fireAllRules();
        assertEquals(3, list.size());
    }
}
