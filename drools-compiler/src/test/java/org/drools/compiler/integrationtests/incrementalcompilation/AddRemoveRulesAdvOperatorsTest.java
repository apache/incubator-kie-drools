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

package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests adding and removing rules with advanced operators.
 */
public class AddRemoveRulesAdvOperatorsTest extends AbstractAddRemoveRulesTest {

    @Test
    public void testAddRemoveSameRuleWithContains() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsSwitchedConstraints() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsAtEnd() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsAtBeg() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    // TODO - the next two tests are same as tests
    // testAddRemoveRuleWithContainsAndExistsAtBeg and testAddRemoveRuleWithContainsAndExistsAtEnd
    // (in each test the rule order is also switched),
    // but keeping them here, because some fails occur sooner in the test here than in previous tests.
    // (in other words, when previous tests fail soon, the fail that will occur later in the test is not
    // visible, until the first one is fixed)
    @Test
    public void testAddRemoveRuleWithContainsAndExistsInFirstRuleAtBeg() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsInFirstRuleAtEnd() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndDoubledExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String() and String()) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithNotContainsAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveRuleWithContainsAndNotContainsAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, getFactsContains(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithMemberOf() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";


        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveSameRuleWithMemberOfSwitchedConstraints() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExistsAtEnd() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExistsAtBeg() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndNotExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     not(exists(Integer() and Integer())) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{memberString}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleWithNotMemberOfAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s not memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s not memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[]{"fact"}, getGlobalsMemberOf("test"));
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndNotMemberOfAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s not memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME,
                new Object[]{memberString, "fact"}, getGlobalsMemberOf(memberString));
    }

    @Test @Ignore
    public void testAddRemoveRuleWithContainsMatchesExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s matches \"tes.*\") \n" +
                "     exists(String()) \n" +
                "     String($s memberOf memberList) \n" +
                " then \n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s matches \"tes.*\") \n" +
                "     String($s memberOf memberList) \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String() and String()) \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME,
                new Object[]{memberString, "1"}, getGlobalsMemberOf(memberString));
    }

    @Test
    public void testAddRemoveRuleContainsExists3Rules() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE3_NAME + "'); \n" +
                "end";

        Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[]{mapFact, 1, "1"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule3})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE3_NAME})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        runAddRemoveTest(builder.build(), additionalGlobals);
    }

    @Test() //(timeout = 10000L)
    public void testAddRemoveRuleContainsExists3RulesDoubledExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    exists( Integer() and Integer() )\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE3_NAME + "'); \n" +
                "end";

        Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[]{mapFact, 1, "1"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule3})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE3_NAME})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        runAddRemoveTest(builder.build(), additionalGlobals);
    }

    private Object[] getFactsContains() {
        final Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");

        return new Object[]{mapFact, 1, 2, "1"};
    }

    private Map<String, Object> getGlobalsMemberOf(final String memberString) {
        final Map<String, Object> globals = new HashMap<String, Object>(1);
        final List<String> memberList = new ArrayList<String>(1);
        memberList.add(memberString);
        globals.put("memberList", memberList);
        return globals;
    }

    // TODO matches, not matches
}
