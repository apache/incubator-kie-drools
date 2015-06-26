package org.drools.workbench.models.guided.dtree.backend;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class GuidedDecisionTreeDRLPersistenceUnmarshallingLineSeparatorsTest extends AbstractGuidedDecisionTreeDRLPersistenceUnmarshallingTest {

    @Parameterized.Parameters
    public static Collection<String[]> lineSeparators() {
        return Arrays.asList( new String[][]{
                { "\n" }, { "\r\n" }
        } );
    }

    private final String lineSeparator;

    public GuidedDecisionTreeDRLPersistenceUnmarshallingLineSeparatorsTest( final String lineSeparator ) {
        this.lineSeparator = lineSeparator;
    }

    @Test
    public void testRuleDifferentLineSeparators() {
        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final String drl = "rule \"test\" " + SEPARATOR_PARAM +
                "when " + SEPARATOR_PARAM +
                "  Person( integerField == " + VALUE_PARAM + " ) " + SEPARATOR_PARAM +
                "then " + SEPARATOR_PARAM +
                "end";

        getAndTestUnmarshalledModel( drl.replace( SEPARATOR_PARAM, lineSeparator ).replace( VALUE_PARAM, "10" ), "test", 0 );
        getAndTestUnmarshalledModel( drl.replace( SEPARATOR_PARAM, lineSeparator ).replace( VALUE_PARAM, "\"something\"" ), "test", 1 );
    }
}
