package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class AddRemoveGenerated2RulesMapContainsTest extends AbstractAddRemoveGenerated2RulesTest {

    public AddRemoveGenerated2RulesMapContainsTest(final ConstraintsPair constraintsPair) {
        super(constraintsPair);
    }

    @Parameterized.Parameters
    public static Collection<ConstraintsPair[]> getRulesConstraints() {
        return generateRulesConstraintsCombinations(
                " java.util.Map(values() contains \"1\") \n",
                " Integer() \n",
                " exists(Integer() and exists(Integer() and Integer())) \n");
    }
}
