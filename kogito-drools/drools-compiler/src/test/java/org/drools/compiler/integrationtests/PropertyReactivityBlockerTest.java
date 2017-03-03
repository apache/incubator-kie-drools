/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.Address;
import org.drools.compiler.Cell;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Neighbor;
import org.drools.compiler.Person;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.reteoo.ReteDumper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

public class PropertyReactivityBlockerTest extends CommonTestMethodBase {
    
    @Test()
    public void testA_NotWorking() {
        // DROOLS-644
        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "    $p2 : Person( age > $p1.age ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();
        
        ReteDumper.dumpRete(ksession);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        ksession.fireAllRules();
        
        mario.setAge(35);
        ksession.update(fh_mario, mario, "age");
        
        int x = ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("t0", list.get(0));
    }
    
    @Test()
    public void testAbis_NotWorking() {
        // DROOLS-644
        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "    $p2 : Person( age > $p1.age ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n" +
                "rule Z when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "then\n" +
                "    modify($p1) { setAge(35); } \n" +
                "end\n" 
                ;
        
        // making the default explicit:
        KieSession ksession = new KieHelper(PropertySpecificOption.ALWAYS).addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();
        ksession.addEventListener(new DebugAgendaEventListener());
        System.out.println(drl);
        ReteDumper.dumpRete(ksession);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        int x = ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("t0", list.get(0));
    }
    
    @Test()
    public void testA_Working() {
        // DROOLS-644
        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\", $a1: age ) \n" +
                "    $p2 : Person( age > $a1 ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();
        
        ReteDumper.dumpRete(ksession);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        ksession.fireAllRules();
        
        mario.setAge(35);
        ksession.update(fh_mario, mario, "age");
        
        int x = ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("t0", list.get(0));
    }
    
    @Test()
    public void testAbis_Working() {
        // DROOLS-644
        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\", $a1: age) \n" +
                "    $p2 : Person( age > $a1 ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n" +
                "rule Z when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "then\n" +
                "    modify($p1) { setAge(35); } \n" +
                "end\n" 
                ;

        // making the default explicit:
        KieSession ksession = new KieHelper(PropertySpecificOption.ALWAYS).addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();
        
        System.out.println(drl);
        ReteDumper.dumpRete(ksession);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        int x = ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("t0", list.get(0));
    }
    
    @Test()
    public void testUpdateRewrittenWithCorrectBitMaskAndCorrectClass() {
        String drl =
                "import " + Cell.class.getCanonicalName() + ";\n" +
                "import " + Neighbor.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    Neighbor( $n : neighbor ) \n" +
                "then\n" +
                "    modify( $n ) {\n" + 
                "        setValue( $n.getValue() + 1 )\n" + 
                "    }\n" +
                "end\n" +
                "rule C when\n" +
                "    $c: Cell( value > 0 ) \n" +
                "then\n" +
                "   list.add(\"C\"); \n" +
                "end\n" 
                ;
        
        /* The RHS was wrongly rewritten as:
          { org.kie.api.runtime.rule.FactHandle $n__Handle2__ = drools.getFactHandle($n);
            $n.setValue( $n.getValue() + 1 ); 
            drools.update( $n__Handle2__, org.drools.core.util.bitmask.EmptyBitMask.get(), org.drools.compiler.Neighbor.class ); }
        instead of:
          { org.kie.api.runtime.rule.FactHandle $n__Handle2__ = drools.getFactHandle($n);
            $n.setValue( $n.getValue() + 1 ); 
            drools.update( $n__Handle2__, new org.drools.core.util.bitmask.LongBitMask(16L), org.drools.compiler.Cell.class ); }
         */

        // making the default explicit:
        KieSession ksession = new KieHelper(PropertySpecificOption.ALWAYS).addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();
        
        System.out.println(drl);
        ReteDumper.dumpRete(ksession);
        ksession.addEventListener(new DebugAgendaEventListener());

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Cell c0 = new Cell(0);
        Cell c1 = new Cell(0);
        Neighbor n = new Neighbor(c0, c1);
        System.out.println("c0: "+c0);
        System.out.println("c1: "+c1);
        System.out.println("n:" +n);
        
        ksession.insert(c0);
        ksession.insert(c1);
        ksession.insert(n);
        int x = ksession.fireAllRules();
        
        System.out.println("from outside:");
        System.out.println("c0: "+c0);
        System.out.println("c1: "+c1);
        System.out.println("n:" +n);
        assertEquals(1, list.size());
        assertEquals("C", list.get(0));
    }
}
