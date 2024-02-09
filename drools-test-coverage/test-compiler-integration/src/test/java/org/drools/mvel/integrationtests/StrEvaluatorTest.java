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

import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.RoutingMessage;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class StrEvaluatorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public StrEvaluatorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testStrStartsWith() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue("R1:messageBody");

            ksession.insert(m);
            ksession.fireAllRules();
            assertThat(list.size() == 4).isTrue();

            assertThat(list.get(0).equals("Message starts with R1")).isTrue();
            assertThat(list.get(1).equals("Message length is not 17")).isTrue();
            assertThat(list.get(2).equals("Message does not start with R2")).isTrue();
            assertThat(list.get(3).equals("Message does not end with R1")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrEndsWith() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue("messageBody:R2");

            ksession.insert(m);
            ksession.fireAllRules();
            assertThat(list.size() == 4).isTrue();

            assertThat(list.get(0).equals("Message ends with R2")).isTrue();
            assertThat(list.get(1).equals("Message length is not 17")).isTrue();
            assertThat(list.get(2).equals("Message does not start with R2")).isTrue();
            assertThat(list.get(3).equals("Message does not end with R1")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrLengthEquals() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue( "R1:messageBody:R2" );

            ksession.insert( m );
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(6);
            assertThat(list.contains("Message length is 17")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrNotStartsWith() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue("messageBody");

            ksession.insert(m);
            ksession.fireAllRules();
            assertThat(list.size() == 3).isTrue();
            assertThat(list.get(1).equals("Message does not start with R2")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrNotEndsWith() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue("messageBody");

            ksession.insert(m);
            ksession.fireAllRules();
            assertThat(list.size() == 3).isTrue();
            assertThat(list.get(0).equals("Message length is not 17")).isTrue();
            assertThat(list.get(1).equals("Message does not start with R2")).isTrue();
            assertThat(list.get(2).equals("Message does not end with R1")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrLengthNoEquals() {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        try {
            List list = new ArrayList();
            ksession.setGlobal( "list", list );

            RoutingMessage m = new RoutingMessage();
            m.setRoutingValue("messageBody");

            ksession.insert(m);
            ksession.fireAllRules();
            assertThat(list.size() == 3).isTrue();

            assertThat(list.get(0).equals("Message length is not 17")).isTrue();
            assertThat(list.get(1).equals("Message does not start with R2")).isTrue();
            assertThat(list.get(2).equals("Message does not end with R1")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrWithLogicalOr() {
        String drl = "package org.drools.mvel.integrationtests\n"
                     + "import org.drools.mvel.compiler.RoutingMessage\n"
                     + "rule R1\n"
                     + " when\n"
                     + " RoutingMessage( routingValue == \"R2\" || routingValue str[startsWith] \"R1\" )\n"
                     + " then\n"
                     + "end\n";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        KieSession ksession = kbase.newKieSession();
        try {
            for (String msgValue : new String[]{ "R1something", "R2something", "R2" }) {
                RoutingMessage msg = new RoutingMessage();
                msg.setRoutingValue(msgValue);
                ksession.insert(msg);
            }

            assertThat(ksession.fireAllRules()).as("Wrong number of rules fired").isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrWithInlineCastAndFieldOnThis() {
        String drl = "package org.drools.mvel.integrationtests " +
                     "import " + Person.class.getName() + "; " +
                     "rule R1 " +
                     " when " +
                     " Object( this#" + Person.class.getName() + ".name str[startsWith] \"M\" ) " +
                     " then " +
                     "end ";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( new Person( "Mark" ) );

            assertThat(ksession.fireAllRules()).as("Wrong number of rules fired").isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrWithInlineCastOnThis() {
        String drl = "package org.drools.mvel.integrationtests " +
                     "rule R1 " +
                     " when " +
                     " Object( this#String str[startsWith] \"M\" ) " +
                     " then " +
                     "end ";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( "Mark" );

            assertThat(ksession.fireAllRules()).as("Wrong number of rules fired").isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    private KieBase readKnowledgeBase() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "strevaluator_test.drl");
        return kbase;
    }

    @Test
    public void testUrlInStringComparison() {
        // DROOLS-6983
        String drl = "package org.drools.mvel.integrationtests " +
                "import " + FactMap.class.getCanonicalName() + "; " +
                "rule R1 " +
                " when " +
                " FactMap( String.valueOf(this.getElement(\"classHistory[0].class.where(system='http://domain/url/Code').exists()\")) == \"1\" ) " +
                " then " +
                "end ";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        KieSession ksession = kbase.newKieSession();
        try {
            Map<String, Integer> map = new HashMap<>();
            map.put( "classHistory[0].class.where(system='http://domain/url/Code').exists()", 1 );
            ksession.insert( new FactMap( map ) );

            assertThat(ksession.fireAllRules()).as("Wrong number of rules fired").isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    public static class FactMap<K,V> {
        private final Map<K,V> map;

        public FactMap(Map<K,V> map) {
            this.map = map;
        }

        public V getElement(K key) {
            return map.get(key);
        }
    }
}
