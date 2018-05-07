package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.test.testcategory.TurtleTestCategory;

@Category(TurtleTestCategory.class)
@RunWith(Parameterized.class)
public class AddRemoveRulesAddDeleteFactsTest {

    private final StringPermutation rulesPermutation;

    public AddRemoveRulesAddDeleteFactsTest(final StringPermutation rulesPermutation) {
        this.rulesPermutation = rulesPermutation;
    }

    @Parameterized.Parameters
    public static Collection<StringPermutation[]> getRulesPermutations() {
        final Collection<StringPermutation[]> rulesPermutations = new HashSet<>();

        final Set<StringPermutation> parametersPermutations = new HashSet<>();
        getStringPermutations(
                new String[]{TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME},
                new String[]{},
                parametersPermutations);

        for (final StringPermutation permutation : parametersPermutations) {
            rulesPermutations.add(new StringPermutation[]{permutation});
        }

        return rulesPermutations;
    }

    @Test
    public void testAddRemoveRulesAddRemoveFacts() {
        final KieSession kieSession = TestUtil.buildSessionInSteps(getRules());
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            final List<FactHandle> insertedFacts = TestUtil.insertFacts(kieSession, getFacts());
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rulesPermutation.getPermutation());
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeFacts(kieSession, insertedFacts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    private Object[] getFacts() {
        return new Object[]{1, "1"};
    }

    private static void getStringPermutations(final String[] rules, final String[] partialPermutation,
            final Collection<StringPermutation> foundPermutations) {
        if (rules.length == 1) {
            if (partialPermutation != null && partialPermutation.length > 0) {
                foundPermutations.add(new StringPermutation(concatenateStringArrays(partialPermutation, rules)));
            } else {
                foundPermutations.add(new StringPermutation(rules));
            }
        } else {
            for (int i = 0; i < rules.length; i++) {
                if (i == 0) {
                    getStringPermutations(
                            Arrays.copyOfRange(rules, 1, rules.length),
                            concatenateStringArrays(partialPermutation, new String[]{rules[i]}),
                            foundPermutations);
                } else if (i == rules.length - 1) {
                    getStringPermutations(
                            Arrays.copyOfRange(rules, 0, rules.length - 1),
                            concatenateStringArrays(partialPermutation, new String[]{rules[i]}),
                            foundPermutations);
                } else {
                    final String[] remainingRules = concatenateStringArrays(
                            Arrays.copyOfRange(rules, 0, i),
                            Arrays.copyOfRange(rules, i + 1, rules.length));

                    getStringPermutations(
                            remainingRules,
                            concatenateStringArrays(partialPermutation, new String[]{rules[i]}),
                            foundPermutations);
                }
            }
        }
    }

    private static String[] concatenateStringArrays(final String[] array1, final String[] array2) {
        final String[] concatenatedArray = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, concatenatedArray, 0, array1.length);
        System.arraycopy(array2, 0, concatenatedArray, array1.length, array2.length);
        return concatenatedArray;
    }

    private static String[] getRules() {
        final String[] rules = new String[3];

        rules[0] = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        rules[1] = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        rules[2] = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE3_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                "   String() \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                " end";

        return rules;
    }
}