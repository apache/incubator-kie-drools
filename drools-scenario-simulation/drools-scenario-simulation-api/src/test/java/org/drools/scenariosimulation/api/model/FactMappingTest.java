package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

public class FactMappingTest {

    private FactMapping original;

    @Test
    public void cloneFactMapping() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        original.addExpressionElement("FIRST_STEP", String.class.getName());
        original.setExpressionAlias("EA_TEST");
        original.setGenericTypes(new ArrayList<>());
        
        assertThat(original.cloneFactMapping()).isEqualTo(original);
    }

    @Test
    public void getExpressionElementsWithoutClass_missingExpression() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        
        assertThatThrownBy(original::getExpressionElementsWithoutClass)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ExpressionElements malformed");
        assertThat(original.getExpressionElements()).hasSize(0);

    }
    
    @Test
    public void getExpressionElementsWithoutClass_properlyFormed() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        original.addExpressionElement("STEP", String.class.getCanonicalName());

        assertThat(original.getExpressionElementsWithoutClass()).hasSize(0);
        assertThat(original.getExpressionElements()).hasSize(1);
    }

}