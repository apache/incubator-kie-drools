package org.drools.mvelcompiler.bigdecimal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.expr.AssignExpr;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.BigDecimalExprT;
import org.drools.mvelcompiler.ast.TypedExpression;

public class BigDecimalConversion {

    public Optional<TypedExpression> convertAssignExpr(AssignExpr assignExpr,
                                                       TypedExpression target,
                                                       TypedExpression value) {

        Optional<Type> optRHSType = value.getType();
        if(!optRHSType.isPresent()) {
            return Optional.empty();
        }

        AssignExpr.Operator operator = assignExpr.getOperator();

        if(operator == AssignExpr.Operator.ASSIGN) {
            return Optional.empty();
        }

        if (target.getType().filter(t -> t == BigDecimal.class).isPresent()) {

            String bigDecimalMethod = BigDecimalExprT.toBigDecimalMethod(operator.toString());
            BigDecimalExprT convertedBigDecimalExpr = new BigDecimalExprT(bigDecimalMethod, target, value);
            return Optional.of(new AssignExprT(AssignExpr.Operator.ASSIGN, target, convertedBigDecimalExpr));
        }
        return Optional.empty();
    }
}



