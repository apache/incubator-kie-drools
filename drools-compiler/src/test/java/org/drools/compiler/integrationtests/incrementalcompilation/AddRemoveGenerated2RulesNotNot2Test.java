package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesNotNot2Test extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesNotNot2Test(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        return generateRulesConstraintsCombinations(
                " Integer() \n",
                " not(not(exists(Integer() and Integer()))) \n",
                " exists(Integer() and Integer()) \n");
    }
}
