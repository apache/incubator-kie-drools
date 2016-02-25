package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesIntegerTest extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesIntegerTest(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        return generateRulesConstraintsCombinations(
                " Integer() \n",
                " exists(Integer() and Integer()) \n",
                " exists(Integer() and exists(Integer() and Integer())) \n");
    }
}
