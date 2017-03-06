/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.testcoverage.common.listener.OrderListener;
import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Sample;
import org.drools.testcoverage.common.model.Subject;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.DecisionTableInputType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests all features which can be used in decision tables.
 */
public class DecisionTableTest {

    @Test
    public void testSimpleXLS() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sample.xls", getClass(), DecisionTableInputType.XLS);
        testSimpleDecisionTable(decisionTable);
    }

    @Test
    public void testSimpleCSV() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sample.csv", getClass(), DecisionTableInputType.CSV);
        testSimpleDecisionTable(decisionTable);
    }

    private void testSimpleDecisionTable(final Resource decisionTable) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        final KieSession session = kbase.newKieSession();

        final Person person = new Person("Paul");
        person.setId(1);
        Assertions.assertThat(person.getName()).isEqualTo("Paul");
        Assertions.assertThat(person.getId()).isEqualTo(1);

        session.insert(person);
        session.fireAllRules();

        Assertions.assertThat(person.getName()).isEqualTo("Paul");
        Assertions.assertThat(person.getId()).isEqualTo(2);

        session.dispose();
    }

    @Test
    public void testMultipleTableXLS() {

        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("multiple_tables.xls", getClass(), DecisionTableInputType.XLS);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        Assertions.assertThat(2).isEqualTo(kbase.getKiePackages().size());

        final KieSession session = kbase.newKieSession();

        // testing person object from the first table
        final Person person = new Person("Paul");
        person.setId(1);
        Assertions.assertThat(person.getName()).isEqualTo("Paul");
        Assertions.assertThat(person.getId()).isEqualTo(1);

        // testing second person, he should be renamed by rules in the second
        // table
        final Person person2 = new Person("Helmut von Seireit");
        person2.setId(1000);
        Assertions.assertThat(person2.getName()).isEqualTo("Helmut von Seireit");
        Assertions.assertThat(person2.getId()).isEqualTo(1000);

        session.insert(person);
        session.insert(person2);
        session.fireAllRules();

        Assertions.assertThat(person.getName()).isEqualTo("Paul");
        Assertions.assertThat(person.getId()).isEqualTo(2);
        Assertions.assertThat(person2.getName()).isEqualTo("Wilhelm von Seireit");
        Assertions.assertThat(person2.getId()).isEqualTo(1000);

        session.dispose();
    }

    /**
     * test for various evaluations, file sample_eval_dt.xls need to rewrite xls
     * table and maybe add some classes to be able to do the test
     */
    @Test
    public void testEvalTable() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("eval_dt.xls", getClass(), DecisionTableInputType.XLS);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        Assertions.assertThat(2).isEqualTo(kbase.getKiePackages().size());

        KieSession session = kbase.newKieSession();

        final TrackingAgendaEventListener rulesFired = new TrackingAgendaEventListener();
        session.addEventListener(rulesFired);
        rulesFired.clear();

        // eval test 1
        final Subject mary = new Subject("Mary");
        mary.setDummy(1);
        session.insert(mary);
        session.fireAllRules();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest1")).isTrue();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest2")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest3")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest4")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("simpleBindingTest")).isFalse();
        session.dispose();

        // eval test 2
        session = kbase.newKieSession();
        session.addEventListener(rulesFired);
        rulesFired.clear();
        final Subject inge = new Subject("Inge");
        inge.setAge(7);
        inge.setSex("F");
        final Subject jochen = new Subject("Jochen");
        jochen.setAge(9);
        jochen.setSex("M");
        session.insert(inge);
        session.insert(jochen);
        session.fireAllRules();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest1")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest2")).isTrue();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest3")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest4")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("simpleBindingTest")).isFalse();
        session.dispose();

        // eval test 3, will run four times, there are four combinations
        session = kbase.newKieSession();
        session.addEventListener(rulesFired);
        rulesFired.clear();
        final Subject karl = new Subject("Karl");
        karl.setSex("male");
        final Subject egon = new Subject("Egon");
        egon.setSex("male");
        session.insert(karl);
        session.insert(egon);
        session.fireAllRules();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest1")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest2")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest3")).isTrue();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest4")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("simpleBindingTest")).isFalse();
        session.dispose();

        // eval test 4
        session = kbase.newKieSession();
        session.addEventListener(rulesFired);
        rulesFired.clear();
        final Subject gerda = new Subject("Gerda");
        gerda.setSex("female");
        gerda.setAge(9);
        gerda.setDummy(-10);
        session.insert(gerda);
        session.fireAllRules();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest1")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest2")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest3")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest4")).isTrue();
        Assertions.assertThat(rulesFired.isRuleFired("simpleBindingTest")).isFalse();
        session.dispose();

        // eval test 5 - simple binding
        session = kbase.newKieSession();
        session.addEventListener(rulesFired);
        rulesFired.clear();
        final List<Sample> results = new ArrayList<Sample>();
        session.setGlobal("results", results);
        final Sample sample = new Sample();
        session.insert(sample);
        session.fireAllRules();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest1")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest2")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest3")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("evalTest4")).isFalse();
        Assertions.assertThat(rulesFired.isRuleFired("simpleBindingTest")).isTrue();
        session.dispose();
    }

    /**
     * test for advanced rule settings (no-loop, saliences, ...), file
     * sample_advanced_dt.xls
     *
     * covers also bugfix for Bug724257 (agenda group not added from dtable to
     * .drl)
     */
    @Test
    public void testAdvancedTable() {

        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("advanced_dt.xls", getClass(), DecisionTableInputType.XLS);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);
        KieSession session = kbase.newKieSession();

        final OrderListener listener = new OrderListener();
        session.addEventListener(listener);

        final Subject lili = new Subject("Lili");
        lili.setAge(100);
        final Sample sample = new Sample();
        session.insert(lili);
        session.insert(sample);
        session.fireAllRules();

        // just 4 rules should fire
        Assertions.assertThat(listener.size()).isEqualTo(4);

        // rules have to be fired in expected order
        final String[] expected = new String[] { "HelloWorld_11", "namedRule", "b1", "another rule" };
        for (int i = 0; i < 4; i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        session.dispose();
    }

    @Test
    public void testPushQueryWithFactDeclaration() throws IllegalAccessException, InstantiationException {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("queries.xls", getClass(), DecisionTableInputType.XLS);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        final FactType locationType = kbase.getFactType(TestConstants.PACKAGE_FUNCTIONAL, "Location");

        final KieSession ksession = kbase.newKieSession();
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);

        final Person peter = new Person("Peter");
        peter.setLikes("steak");
        final Object steakLocation = locationType.newInstance();
        locationType.set(steakLocation, "thing", "steak");
        locationType.set(steakLocation, "location", "table");
        final Object tableLocation = locationType.newInstance();
        locationType.set(tableLocation, "thing", "table");
        locationType.set(tableLocation, "location", "office");
        ksession.insert(peter);
        final FactHandle steakHandle = ksession.insert(steakLocation);
        final FactHandle tableHandle = ksession.insert(tableLocation);
        ksession.insert("push");
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        listener.clear();

        // when location is changed of what Peter likes, push query should fire
        // rule
        final Object steakLocation2 = locationType.newInstance();
        locationType.set(steakLocation2, "thing", "steak");
        locationType.set(steakLocation2, "location", "desk");
        final Object deskLocation = locationType.newInstance();
        locationType.set(deskLocation, "thing", "desk");
        locationType.set(deskLocation, "location", "office");
        ksession.insert(steakLocation2);
        ksession.insert(deskLocation);
        ksession.delete(steakHandle);
        ksession.delete(tableHandle);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        listener.clear();

        final Person paul = new Person("Paul");
        paul.setLikes("steak");
        ksession.insert(paul);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        listener.clear();

        ksession.dispose();
    }

    @Test
    public void testPullQueryWithFactDeclaration() throws IllegalAccessException, InstantiationException {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("queries.xls", getClass(), DecisionTableInputType.XLS);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        final FactType locationType = kbase.getFactType(TestConstants.PACKAGE_FUNCTIONAL, "Location");

        final KieSession ksession = kbase.newKieSession();
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);

        final Person peter = new Person("Peter");
        peter.setLikes("steak");
        final Object steakLocation = locationType.newInstance();
        locationType.set(steakLocation, "thing", "steak");
        locationType.set(steakLocation, "location", "table");
        final Object tableLocation = locationType.newInstance();
        locationType.set(tableLocation, "thing", "table");
        locationType.set(tableLocation, "location", "office");
        ksession.insert(peter);
        final FactHandle steakHandle = ksession.insert(steakLocation);
        final FactHandle tableHandle = ksession.insert(tableLocation);
        ksession.insert("pull");
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        // when location is changed of what Peter likes, pull query should
        // ignore it
        final Object steakLocation2 = locationType.newInstance();
        locationType.set(steakLocation2, "thing", "steak");
        locationType.set(steakLocation2, "location", "desk");
        final Object deskLocation = locationType.newInstance();
        locationType.set(deskLocation, "thing", "desk");
        locationType.set(deskLocation, "location", "office");
        ksession.insert(steakLocation2);
        ksession.insert(deskLocation);
        ksession.delete(steakHandle);
        ksession.delete(tableHandle);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        final Person paul = new Person("Paul");
        paul.setLikes("steak");
        ksession.insert(paul);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        ksession.dispose();
    }

    /**
     * Test sequential turned on, it overrides all user defined saliences.
     */
    @Test
    public void testSequential() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sequential.csv", getClass(), DecisionTableInputType.CSV);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        final KieSession ksession = kbase.newKieSession();
        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.insert("something");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).as("Wrong number of rules fired").isEqualTo(3);
        final String[] expected = { "Rule1", "Rule2", "Rule3" };
        for (int i = 0; i < 3; i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }
    }

    @Test
    public void testLockOnActive() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("agenda-group.csv", getClass(), DecisionTableInputType.CSV);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);
        final KieSession ksession = kbase.newKieSession();
        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.insert("lockOnActive");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(3);
        final String[] expected = { "a", "a2", "a3" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }
    }

    /**
     * Agenda group rule with auto focus can fire a give focus to agenda group
     * without focus set on whole agenda group.
     */
    @Test
    public void testAutoFocus() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("agenda-group.csv", getClass(), DecisionTableInputType.CSV);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);
        final KieSession ksession = kbase.newKieSession();
        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first test - we try to fire rule in agenda group which has auto focus
        // disable, we won't succeed
        final FactHandle withoutAutoFocus = ksession.insert("withoutAutoFocus");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(0);

        // second test - we try to fire rule in agenda group with auto focus
        // enabled
        // it fires and it's defined consequence causes to fire second rule
        // which has no auto focus
        ksession.insert("autoFocus");
        ksession.delete(withoutAutoFocus);
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = {"b2", "b1"};
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }
    }

    @Test
    public void testActivationGroup() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("agenda-group.csv", getClass(), DecisionTableInputType.CSV);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);
        final KieSession ksession = kbase.newKieSession();
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);

        // only one rule from activation group may fire
        ksession.insert("activationGroup");
        ksession.fireAllRules();
        Assertions.assertThat(listener.isRuleFired("c1")).isFalse();
        Assertions.assertThat(listener.isRuleFired("c2")).isTrue();
        Assertions.assertThat(listener.isRuleFired("c3")).isFalse();
    }

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyConditionInXLS() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("emptyCondition.xls", getClass(), DecisionTableInputType.XLS);
        KieUtil.getKieBuilderFromResources(true, decisionTable);
    }

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyActionInCSV() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("emptyAction.csv", getClass(), DecisionTableInputType.CSV);
        KieUtil.getKieBuilderFromResources(true, decisionTable);
    }

    @Test
    public void testCSVWithDateAttributes() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sample_dates.csv", getClass(), DecisionTableInputType.CSV);

        testDecisionTableWithDateAttributes(decisionTable);
    }

    @Test
    public void testXLSWithDateAttributes() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sample_dates.xls", getClass(), DecisionTableInputType.XLS);

        testDecisionTableWithDateAttributes(decisionTable);
    }

    private void testDecisionTableWithDateAttributes(final Resource decisionTable) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        final ArrayList<String> names = new ArrayList<String>();
        final Collection<KiePackage> pkgs = kbase.getKiePackages();
        for (KiePackage kp : pkgs) {
            names.add(kp.getName());
        }

        Assertions.assertThat(names.contains(TestConstants.PACKAGE_FUNCTIONAL)).isTrue();
        Assertions.assertThat(names.contains(TestConstants.PACKAGE_TESTCOVERAGE_MODEL)).isTrue();

        final KiePackage kiePackage = (KiePackage) pkgs.toArray()[names.indexOf(TestConstants.PACKAGE_FUNCTIONAL)];

        Assertions.assertThat(kiePackage.getRules().size()).isEqualTo(3);
    }
}
