package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesEval2Test extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesEval2Test(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        // Placeholder is replaced by actual variable name during constraints generation.
        // This is needed, because when generator generates the same constraint 3-times for a rule,
        // in each constraint must be different variable name, so Drools can process it
        // (variable is "global" in scope of the rule).
        return generateRulesConstraintsCombinations(
                " Integer() \n",
                " ${variableNamePlaceholder}: Integer() eval(${variableNamePlaceholder} == 1) \n",
                " ${variableNamePlaceholder}: Integer() and (eval(true) or eval(${variableNamePlaceholder} == 1) )\n");
    }
}
