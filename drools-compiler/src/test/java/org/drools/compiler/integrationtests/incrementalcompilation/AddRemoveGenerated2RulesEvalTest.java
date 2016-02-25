package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesEvalTest extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesEvalTest(final ConstraintsPair constraintsPair) {
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
                // Fails on MVEL NPE
                //  " Integer(${variableNamePlaceholder}: this.intValue(), eval(${variableNamePlaceholder} == 1)) \n",
                " exists(Integer() and exists(Integer() and Integer())) \n");
    }
}
