package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAddRemoveGenerated2RulesTest extends AbstractAddRemoveRulesTest {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractAddRemoveGenerated2RulesTest.class);

    private final String rule1;
    private final String rule2;

    public AbstractAddRemoveGenerated2RulesTest(final ConstraintsPair constraintsPair) {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                " when \n ${constraints} " +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                " when \n ${constraints} " +
                "then\n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end\n";

        this.rule1 = rule1.replace("${constraints}", constraintsPair.getConstraints1());
        this.rule2 = rule2.replace("${constraints}", constraintsPair.getConstraints2());
    }

    // This takes only three different constraints - this is intentional, because it is needed to
    // keep the number of combinations at reasonable number.
    public static Collection<ConstraintsPair[]> generateRulesConstraintsCombinations(final String constraint1,
            final String constraint2, final String constraint3) {
        final Set<ConstraintsPair> constraintsPairs = new HashSet<ConstraintsPair>();
        final List<ConstraintsPair[]> result = new ArrayList<ConstraintsPair[]>();

        final List<String> constraintsList = new ArrayList<String>();
        constraintsList.add(constraint1);
        constraintsList.add(constraint2);
        constraintsList.add(constraint3);
        final List<String> constraintsCombinations = getConstraintsCombinations(constraintsList);

        for (String constraintsRule1 : constraintsCombinations) {
            for (String constraintsRule2 : constraintsCombinations) {
                final ConstraintsPair constraintsPair = new ConstraintsPair(constraintsRule1, constraintsRule2);
                if (constraintsPairs.add(constraintsPair)) {
                    result.add(new ConstraintsPair[]{constraintsPair});
                }
            }
        }
        return result;
    }

    private static List<String> getConstraintsCombinations(final List<String> constraintsList) {
        final List<String> ruleConstraintsCombinations = new ArrayList<String>();
        for (String constraint : constraintsList) {
            for (String constraint2 : constraintsList) {
                for (String constraint3 : constraintsList) {
                    ruleConstraintsCombinations.add(constraint.replace("${variableNamePlaceholder}", "$i")
                            + constraint2.replace("${variableNamePlaceholder}", "$j")
                            + constraint3.replace("${variableNamePlaceholder}", "$k"));
                }
            }
        }
        return ruleConstraintsCombinations;
    }

    /////////////////////////// TESTS //////////////////////////////////

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsFireRulesRemoveRulesTestPlan(
                        rule1, rule2, RULE1_NAME, RULE2_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesRevertedRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsFireRulesRemoveRulesTestPlan(
                        rule2, rule1, RULE2_NAME, RULE1_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testFireRulesInsertFactsFireRulesRemoveRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createFireRulesInsertFactsFireRulesRemoveRulesTestPlan(
                        rule1, rule2, RULE1_NAME, RULE2_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testFireRulesInsertFactsFireRulesRemoveRulesRevertedRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createFireRulesInsertFactsFireRulesRemoveRulesTestPlan(
                        rule2, rule1, RULE2_NAME, RULE1_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsRemoveRulesFireRulesRemoveRulesTestPlan(
                        rule1, rule2, RULE1_NAME, RULE2_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRulesRevertedRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsRemoveRulesFireRulesRemoveRulesTestPlan(
                        rule2, rule1, RULE2_NAME, RULE1_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsFireRulesRemoveRulesReinsertRulesTestPlan(
                        rule1, rule2, RULE1_NAME, RULE2_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRulesRevertedRules() {
        checkRunTurtleTests();
        final List<List<TestOperation>> testPlans =
                AddRemoveTestBuilder.createInsertFactsFireRulesRemoveRulesReinsertRulesTestPlan(
                        rule2, rule1, RULE2_NAME, RULE1_NAME, getFacts());

        runAddRemoveTests(testPlans, new HashMap<String, Object>());
    }

    private Object[] getFacts() {
        final Map<Object, String> mapFact = new HashMap<Object, String>(1);
        mapFact.put(new Object(), "1");
        return new Object[] {1, 2, 3, "1", mapFact};
    }
}
