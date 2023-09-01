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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.reteoo.ReteDumper;
import org.drools.mvel.compiler.Cell;
import org.drools.mvel.compiler.Neighbor;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PropertyReactivityBlockerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PropertyReactivityBlockerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
    
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
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
        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put("drools.propertySpecific", "ALWAYS");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration,
                                                                 kieModuleConfigurationProperties,
                                                                 drl);
        final KieSession ksession = kbase.newKieSession();

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
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        
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
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
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
        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put("drools.propertySpecific", "ALWAYS");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration,
                                                                 kieModuleConfigurationProperties,
                                                                 drl);
        final KieSession ksession = kbase.newKieSession();

        System.out.println(drl);
        ReteDumper.dumpRete(ksession);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        int x = ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
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
            drools.update( $n__Handle2__, org.drools.core.util.bitmask.EmptyBitMask.get(), org.drools.mvel.compiler.Neighbor.class ); }
        instead of:
          { org.kie.api.runtime.rule.FactHandle $n__Handle2__ = drools.getFactHandle($n);
            $n.setValue( $n.getValue() + 1 ); 
            drools.update( $n__Handle2__, new org.drools.core.util.bitmask.LongBitMask(16L), org.drools.mvel.compiler.Cell.class ); }
         */

        // making the default explicit:
        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put("drools.propertySpecific", "ALWAYS");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration,
                                                                 kieModuleConfigurationProperties,
                                                                 drl);
        final KieSession ksession = kbase.newKieSession();

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
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("C");
    }
}
