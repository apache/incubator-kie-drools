package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesNotNotTest extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesNotNotTest(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        return generateRulesConstraintsCombinations(
                " Integer() \n",
                " Integer() not(not(exists(Integer() and Integer()))) \n",
                " exists(Integer() and exists(Integer() and Integer())) \n");
    }
}
