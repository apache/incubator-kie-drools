package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesTest extends AbstractAddRemoveRulesTest {

    private final String rule1;
    private final String rule2;

    public AddRemoveGenerated2RulesTest(final ConstraintsPair constraintsPair) {
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

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> addedNumbers() {
        final Set<ConstraintsPair> constraintsPairs = new HashSet<ConstraintsPair>();
        final List<ConstraintsPair[]> result = new ArrayList<ConstraintsPair[]>();
        final List<String> constraintsCombinations = getConstraintsCombinations();

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

    @Test
    public void testAddRemoveGeneratedRules() {
        // TODO use logger instead.
        System.out.println("Rule1: \n" + rule1);
        System.out.println("Rule2: \n" + rule2);

        runAddRemoveTests(rule1, rule2, RULE1_NAME, RULE2_NAME, new Object[] {1, 2, 3, "1"},
                new HashMap<String, Object>());
    }

    private static List<String> getConstraintsCombinations() {
        final List<String> constraints = new ArrayList<String>();
        final List<String> constraintsList = getConstraints();
        for (String constraint : constraintsList) {
            for (String constraint2 : constraintsList) {
                for (String constraint3 : constraintsList) {
                    constraints.add(constraint + constraint2 + constraint3);
                }
            }
        }
        return constraints;
    }

    private static List<String> getConstraints() {
        final List<String> constraintsList = new ArrayList<String>();
        constraintsList.add(" Integer() \n");
        constraintsList.add(" exists(Integer() and Integer()) \n");
        constraintsList.add(" exists(Integer() and exists(Integer() and Integer())) \n");
        return constraintsList;
    }
}
