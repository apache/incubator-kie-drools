package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(Parameterized.class)
public class AddRemoveRulesAddDeleteFactsTest extends AbstractAddRemoveRulesTest {

    private StringPermutation rulesPermutation;

    public AddRemoveRulesAddDeleteFactsTest(final StringPermutation rulesPermutation) {
        this.rulesPermutation = rulesPermutation;
    }

    @Parameterized.Parameters
    public static Collection<StringPermutation[]> getRulesPermutations() {
        final Collection<StringPermutation[]> rulesPermutations = new HashSet<StringPermutation[]>();

        final Set<StringPermutation> parametersPermutations = new HashSet<StringPermutation>();
        getStringPermutations(
                new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME},
                new String[]{},
                parametersPermutations);

        for (StringPermutation permutation : parametersPermutations) {
            rulesPermutations.add(new StringPermutation[]{permutation});
        }

        return rulesPermutations;
    }

    @Test
    public void testAddRemoveRulesAddRemoveFacts() {
        checkRunTurtleTests();
        final List resultsList = new ArrayList();
        final Map<String, Object> sessionGlobals = new HashMap<String, Object>();
        sessionGlobals.put("list", resultsList);
        final TestContext testContext = new TestContext(PKG_NAME_TEST, sessionGlobals, resultsList);
        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();

        builder.addOperation(TestOperationType.CREATE_SESSION, getRules())
                .addOperation(TestOperationType.INSERT_FACTS, getFacts())
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME});

        testContext.executeTestOperations(builder.build());
        builder.clear();

        final Set<FactHandle> insertedFacts = testContext.getActualSessionFactHandles();

        builder.addOperation(TestOperationType.REMOVE_RULES, rulesPermutation.getPermutation())
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_FACTS, insertedFacts.toArray(new FactHandle[]{}))
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        testContext.executeTestOperations(builder.build());
        builder.clear();
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

        rules[0] = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        rules[1] = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        rules[2] = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE3_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                "   String() \n" +
                " then\n" +
                "   list.add('" + RULE3_NAME + "'); \n" +
                " end";

        return rules;
    }
}