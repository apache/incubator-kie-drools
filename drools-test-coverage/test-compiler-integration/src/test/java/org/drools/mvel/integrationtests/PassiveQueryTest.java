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
import java.util.List;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.Rete;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PassiveQueryTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PassiveQueryTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testPassiveQuery() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();


        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("2");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);

        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);

        ksession.insert(2);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat((int) list.get(0)).isEqualTo(2);
    }

    @Test
    public void testPassiveQueryNoDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testPassiveQueryDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void testReactiveQueryDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testPassiveQueryDataDrivenWithBeta() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    $j : Integer( this == $i+1 )\n" +
                "    ?Q( $j; )\n" +
                "then\n" +
                "    list.add( $j );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert("2");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void testPassiveQueryNodeSharing() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1 @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("R2");
    }

    @Test
    public void testPassiveQueryNodeSharing2() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1a @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1a\" );\n" +
                "end\n" +
                "rule R1b @Propagation(IMMEDIATE) when\n" +
                "    Long( $i : intValue )\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2a when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2a\" );\n" +
                "end\n" +
                "rule R2b when\n" +
                "    Long( $i : intValue )\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2b\" );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(1L);
        ksession.insert("1");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.containsAll(asList("R2a", "R2b"))).isTrue();
    }

    @Test
    public void testPassiveQueryUsingSegmentPropagator() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1a @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1a\" );\n" +
                "end\n" +
                "rule R1b @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "    Long( intValue == $i )\n" +
                "then\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1L);
        FactHandle fh = ksession.insert(1);
        ksession.insert("1");

        Rete rete = ((InternalRuleBase)kbase).getRete();
        LeftInputAdapterNode lia = null;

        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( Integer.class == otn.getObjectType().getValueType().getClassType() ) {
                lia = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];
                break;
            }
        }

        LeftTupleSink[] sinks = lia.getSinkPropagator().getSinks();
        QueryElementNode q1 = (QueryElementNode)sinks[0];
        QueryElementNode q2 = (QueryElementNode)sinks[1];

        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        wm.flushPropagations();

        Memory memory1 = wm.getNodeMemory(q1);
        assertThat(memory1.getSegmentMemory().getStagedLeftTuples().isEmpty()).isTrue();

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("R2");

        list.clear();

        ksession.delete(fh);
        ksession.insert(1);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
    }
}
