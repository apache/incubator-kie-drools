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
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        public String getSingleValue() {
            return "a";
        }
    }

    @Test
    public void testFromSharing() {
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

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

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
        String drl =
                "import " + ListsContainer.class.getCanonicalName() + "\n" +
                "global java.util.List out;\n" +
                "rule R1 when\n" +
                "    $list : ListsContainer( )\n" +
                "    $s : String() from $list.singleValue\n" +
                "then\n" +
                "    out.add($s);\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<String> out = new ArrayList<String>();
        ksession.setGlobal( "out", out );

        ksession.insert( new ListsContainer() );
        ksession.fireAllRules();

        assertEquals( 1, out.size() );
        assertEquals( "a", out.get(0) );
    }

    @Test
    public void testFromWithSingleValueAndIncompatibleType() {
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

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }
}
