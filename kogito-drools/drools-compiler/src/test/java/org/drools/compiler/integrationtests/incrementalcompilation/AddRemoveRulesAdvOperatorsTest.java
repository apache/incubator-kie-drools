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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.compiler.TurtleTestCategory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.KieSession;

/**
 * Tests adding and removing rules with advanced operators.
 */
@Category(TurtleTestCategory.class)
public class AddRemoveRulesAdvOperatorsTest {

    @Test
    public void testAddRemoveSameRuleWithContains() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveSameRuleWithContainsSwitchedConstraints() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsAtEnd() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsAtBeg() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    // TODO - the next two tests are same as tests
    // testAddRemoveRuleWithContainsAndExistsAtBeg and testAddRemoveRuleWithContainsAndExistsAtEnd
    // (in each test the rule order is also switched),
    // but keeping them here, because some fails occur sooner in the test here than in previous tests.
    // (in other words, when previous tests fail soon, the fail that will occur later in the test is not
    // visible, until the first one is fixed)
    @Test
    public void testAddRemoveRuleWithContainsAndExistsInFirstRuleAtBeg() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndExistsInFirstRuleAtEnd() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndDoubledExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String() and String()) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithNotContainsAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveRuleWithContainsAndNotContainsAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() not contains \"2\") \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFactsContains());
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFactsContains());
    }

    @Test
    public void testAddRemoveSameRuleWithMemberOf() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";


        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveSameRuleWithMemberOfSwitchedConstraints() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExistsAtEnd() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndExistsAtBeg() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndNotExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     not(exists(Integer() and Integer())) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString);
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString);
    }

    @Test
    public void testAddRemoveRuleWithNotMemberOfAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s not memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s not memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf("test"), "fact");
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf("test"), "fact");
    }

    @Test
    public void testAddRemoveRuleWithMemberOfAndNotMemberOfAndExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     $s: String($s memberOf memberList) \n" +
                "     String() \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     $s: String($s not memberOf memberList) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString, "fact");
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString, "fact");
    }

    @Test @Ignore
    public void testAddRemoveRuleWithContainsMatchesExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s matches \"tes.*\") \n" +
                "     exists(String()) \n" +
                "     String($s memberOf memberList) \n" +
                " then \n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List memberList\n" +
                " global java.util.List list \n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "     String() \n" +
                "     $s: String($s matches \"tes.*\") \n" +
                "     String($s memberOf memberList) \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String() and String()) \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final String memberString = "test";
        AddRemoveTestCases.runAllTestCases(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, getGlobalsMemberOf(memberString), memberString, "1");
        AddRemoveTestCases.runAllTestCases(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, getGlobalsMemberOf(memberString), memberString, "1");
    }

    @Test
    public void testAddRemoveRuleContainsExists3Rules() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                "end";

        Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");

        final KieSession kieSession = TestUtil.createSession(rule1);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            kieSession.setGlobal("globalInt", new AtomicInteger(0));
            TestUtil.insertFacts(kieSession, mapFact, 1, "1");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testAddRemoveRuleContainsExists3RulesDoubledExists() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer() \n" +
                "    exists( Integer() and Integer() )\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    java.util.Map(values() contains \"1\") \n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                "end";

        Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");

        final KieSession kieSession = TestUtil.createSession(rule1);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            kieSession.setGlobal("globalInt", new AtomicInteger(0));
            TestUtil.insertFacts(kieSession, mapFact, 1, "1");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
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
}
