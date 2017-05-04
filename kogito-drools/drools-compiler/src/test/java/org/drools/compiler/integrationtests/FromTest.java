/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
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
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FromTest {

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
}
