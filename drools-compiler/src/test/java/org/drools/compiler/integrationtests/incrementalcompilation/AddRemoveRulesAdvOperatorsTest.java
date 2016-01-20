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

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests adding and removing rules with advanced operators.
 */
public class AddRemoveRulesAdvOperatorsTest extends AbstractAddRemoveRulesTest {

    @Test
    public void testAddRemoveSameRuleWithContains() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";



        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsSwitchedConstraints() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     String() \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsAndExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     exists(String()) \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsAndExistsAtEnd() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsAndExistsInFirstRuleAtBeg() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     exists(String()) \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsAndExistsInFirstRuleAtEnd() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String()) \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    @Test
    public void testAddRemoveSameRuleWithContainsAndDoubledExists() {
        final String rule1 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R1\"\n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                " then \n" +
                "     list.add('R1'); \n" +
                " end";

        final String rule2 = "package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list \n" +
                " rule \"R2\" \n" +
                " when \n" +
                "     String() \n" +
                "     java.util.Map(values() contains \"1\") \n" +
                "     exists(String() and String()) \n" +
                " then\n" +
                "     list.add('R2'); \n" +
                " end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, getFacts(), null);
    }

    private List<Object> getFacts() {
        final List<Object> facts = new ArrayList<Object>();
        Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");
        facts.add(mapFact);
        facts.add("1");

        return facts;
    }
}
