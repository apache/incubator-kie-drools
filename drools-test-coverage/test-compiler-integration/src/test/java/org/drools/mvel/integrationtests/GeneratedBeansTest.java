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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class GeneratedBeansTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GeneratedBeansTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testGeneratedBeans1() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_GeneratedBeans.drl");

        final FactType cheeseFact = kbase.getFactType("org.drools.generatedbeans", "Cheese");
        final Object cheese = cheeseFact.newInstance();
        cheeseFact.set(cheese, "type", "stilton");
        assertThat(cheeseFact.get(cheese, "type")).isEqualTo("stilton");

        final FactType personType = kbase.getFactType("org.drools.generatedbeans", "Person");
        final Object ps = personType.newInstance();
        personType.set(ps, "age", 42);
        final Map<String, Object> personMap = personType.getAsMap(ps);
        assertThat(personMap.get("age")).isEqualTo(42);

        personMap.put("age", 43);
        personType.setFromMap(ps, personMap);
        assertThat(personType.get(ps, "age")).isEqualTo(43);
        assertThat(cheeseFact.getField("type").get(cheese)).isEqualTo("stilton");

        final KieSession ksession = kbase.newKieSession();
        final Object cg = cheeseFact.newInstance();
        ksession.setGlobal("cg", cg);
        final List<Object> result = new ArrayList<Object>();
        ksession.setGlobal("list", result);

        ksession.insert(cheese);
        ksession.fireAllRules();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(5);

        final FactType personFact = kbase.getFactType("org.drools.generatedbeans", "Person");
        final Object person = personFact.newInstance();
        personFact.getField("likes").set(person, cheese);
        personFact.getField("age").set(person, 7);

        ksession.insert(person);
        ksession.fireAllRules();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1)).isEqualTo(person);
    }

    @Test
    public void testGeneratedBeans2() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_GeneratedBeans2.drl");
        final FactType cheeseFact = kbase.getFactType("org.drools.generatedbeans", "Cheese");
        final Object cheese = cheeseFact.newInstance();
        cheeseFact.set(cheese, "type", "stilton");
        assertThat(cheeseFact.get(cheese, "type")).isEqualTo("stilton");

        final Object cheese2 = cheeseFact.newInstance();
        cheeseFact.set(cheese2, "type", "stilton");
        assertThat(cheese2).isEqualTo(cheese);

        final FactType personType = kbase.getFactType("org.drools.generatedbeans", "Person");
        final Object ps = personType.newInstance();
        personType.set(ps, "name", "mark");
        personType.set(ps, "last", "proctor");
        personType.set(ps, "age", 42);

        final Object ps2 = personType.newInstance();
        personType.set(ps2, "name", "mark");
        personType.set(ps2, "last", "proctor");
        personType.set(ps2, "age", 30);
        assertThat(ps2).isEqualTo(ps);

        personType.set(ps2, "last", "little");
        assertThat(ps.equals(ps2)).isFalse();

        final KieSession wm = kbase.newKieSession();
        final Object cg = cheeseFact.newInstance();
        wm.setGlobal("cg", cg);
        final List result = new ArrayList();
        wm.setGlobal("list", result);

        wm.insert(cheese);
        wm.fireAllRules();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(5);

        final FactType personFact = kbase.getFactType("org.drools.generatedbeans", "Person");
        final Object person = personFact.newInstance();
        personFact.getField("likes").set(person, cheese);
        personFact.getField("age").set(person, 7);

        wm.insert(person);
        wm.fireAllRules();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1)).isEqualTo(person);
    }

    @Test
    public void testGeneratedBeansSerializable() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_GeneratedBeansSerializable.drl");

        final FactType cheeseFact = kbase.getFactType("org.drools.generatedbeans", "Cheese");
        assertThat(Serializable.class.isAssignableFrom(cheeseFact.getFactClass())).as("Generated beans must be serializable").isTrue();

        final Object cheese = cheeseFact.newInstance();
        cheeseFact.set(cheese, "type", "stilton");

        final Object cheese2 = cheeseFact.newInstance();
        cheeseFact.set(cheese2, "type", "brie");

        final KieSession ksession = kbase.newKieSession();
        final List<Number> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        ksession.insert(cheese);
        ksession.insert(cheese2);
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(2);
    }
}
