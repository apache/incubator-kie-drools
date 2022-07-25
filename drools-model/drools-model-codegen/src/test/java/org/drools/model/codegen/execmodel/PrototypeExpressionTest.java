package org.drools.model.codegen.execmodel;

import org.drools.model.Prototype;
import org.drools.model.PrototypeExpression;
import org.drools.model.PrototypeFact;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.PrototypeDSL.prototype;
import static org.drools.model.PrototypeExpression.ExpressionBuilder.fixedValue;
import static org.drools.model.PrototypeExpression.ExpressionBuilder.prototypeField;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;

public class PrototypeExpressionTest {

    @Test
    public void testExpression() {
        PrototypeExpression.Expression expr1 = prototypeField("fieldA");
        PrototypeExpression.Expression expr2 = prototypeField("fieldB").add(prototypeField("fieldC")).sub(fixedValue(1));

        Prototype prototype = prototype("test");
        PrototypeFact testFact = (PrototypeFact) createMapBasedFact(prototype);
        testFact.set( "fieldA", 12 );
        testFact.set( "fieldB", 8 );
        testFact.set( "fieldC", 5 );

        assertThat(expr1.asFunction(prototype).apply(testFact)).isEqualTo(expr2.asFunction(prototype).apply(testFact));
        assertThat(expr1.getImpactedFields()).containsExactly("fieldA");
        assertThat(expr2.getImpactedFields()).containsExactlyInAnyOrder("fieldB", "fieldC");
    }
}