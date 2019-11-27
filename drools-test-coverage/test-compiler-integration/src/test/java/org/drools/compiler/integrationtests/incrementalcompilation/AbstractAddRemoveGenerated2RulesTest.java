package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.test.testcategory.TurtleTestCategory;

@Category(TurtleTestCategory.class)
public abstract class AbstractAddRemoveGenerated2RulesTest {

    private final String rule1;
    private final String rule2;

    public AbstractAddRemoveGenerated2RulesTest(final ConstraintsPair constraintsPair) {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n ${constraints} " +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n ${constraints} " +
                "then\n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end\n";

        this.rule1 = rule1.replace("${constraints}", constraintsPair.getConstraints1());
        this.rule2 = rule2.replace("${constraints}", constraintsPair.getConstraints2());
    }

    // This takes only three different constraints - this is intentional, because it is needed to
    // keep the number of combinations at reasonable number.
    public static Collection<ConstraintsPair[]> generateRulesConstraintsCombinations(final String constraint1,
                                                                                     final String constraint2,
                                                                                     final String constraint3) {
        final Set<ConstraintsPair> constraintsPairs = new HashSet<>();
        final List<ConstraintsPair[]> result = new ArrayList<>();

        final List<String> constraintsList = new ArrayList<>();
        constraintsList.add(constraint1);
        constraintsList.add(constraint2);
        constraintsList.add(constraint3);
        final List<String> constraintsCombinations = getConstraintsCombinations(constraintsList);

        for (final String constraintsRule1 : constraintsCombinations) {
            for (final String constraintsRule2 : constraintsCombinations) {
                final ConstraintsPair constraintsPair = new ConstraintsPair(constraintsRule1, constraintsRule2);
                if (constraintsPairs.add(constraintsPair)) {
                    result.add(new ConstraintsPair[]{constraintsPair});
                }
            }
        }
        return result;
    }

    private static List<String> getConstraintsCombinations(final List<String> constraintsList) {
        final List<String> ruleConstraintsCombinations = new ArrayList<>();
        for (final String constraint : constraintsList) {
            for (final String constraint2 : constraintsList) {
                for (final String constraint3 : constraintsList) {
                    ruleConstraintsCombinations.add(constraint.replace("${variableNamePlaceholder}", "$i")
                            + constraint2.replace("${variableNamePlaceholder}", "$j")
                            + constraint3.replace("${variableNamePlaceholder}", "$k"));
                }
            }
        }
        return ruleConstraintsCombinations;
    }

    /////////////////////////// TESTS //////////////////////////////////

    @Test(timeout = 40000)
    public void testInsertFactsFireRulesRemoveRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testInsertFactsFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testFireRulesInsertFactsFireRulesRemoveRules() {
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testFireRulesInsertFactsFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRules() {
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 40000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRulesRevertedRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    private Object[] getFacts() {
        final Map<Object, String> mapFact = new HashMap<>(1);
        mapFact.put(new Object(), "1");
        return new Object[] {1, 2, 3, "1", mapFact};
    }
}
