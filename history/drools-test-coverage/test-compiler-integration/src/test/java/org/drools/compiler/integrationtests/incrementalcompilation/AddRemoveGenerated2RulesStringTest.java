package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesStringTest extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesStringTest(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        return generateRulesConstraintsCombinations(
                " String() \n",
                " exists(String() and String()) \n",
                " exists(String() and exists(String() and String())) \n");
    }
}
