package org.kie.pmml.compiler.commons.factories;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.GET_MODEL;

public class KiePMMLFactoryFactoryTest {

    @Test
    void getInstantiationExpression() {
        final String kiePMMLModelClass = "org.kie.model.ClassModel";
        Expression retrieved = KiePMMLFactoryFactory.getInstantiationExpression(kiePMMLModelClass, true);
        validateNotCodegen(retrieved, kiePMMLModelClass);
        retrieved = KiePMMLFactoryFactory.getInstantiationExpression(kiePMMLModelClass, false);
        validateCodegen(retrieved, kiePMMLModelClass);
    }

    private void validateNotCodegen(Expression toValidate, String kiePMMLModelClass) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate).isInstanceOf(MethodCallExpr.class);
        MethodCallExpr methodCallExpr = (MethodCallExpr) toValidate;
        assertThat(methodCallExpr.getScope().get().asNameExpr().toString()).isEqualTo(kiePMMLModelClass);
        assertThat(methodCallExpr.getName().asString()).isEqualTo(GET_MODEL);
    }

    private void validateCodegen(Expression toValidate, String kiePMMLModelClass) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate).isInstanceOf(ObjectCreationExpr.class);
        ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) toValidate;
        assertThat(objectCreationExpr.getType().asString()).isEqualTo(kiePMMLModelClass);
    }
}