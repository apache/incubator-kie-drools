package org.drools.mvelcompiler.bigdecimal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.expr.AssignExpr;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class BigDecimalConversion {
    TypedExpression rhs;

    BigDecimalConversion(TypedExpression rhs) {
        this.rhs = rhs;
    }

    public static BigDecimalConversion shouldConvertPlusEqualsOperatorBigDecimal(AssignExpr n, Optional<TypedExpression> optRHS) {
        if(!optRHS.isPresent() || (!optRHS.get().getType().isPresent())) {
            return new DoNotConvert(null);
        }

        TypedExpression rhs = optRHS.get();
        Optional<Type> optRHSType = rhs.getType();
        if(!optRHSType.isPresent()) {
            return new DoNotConvert(null);
        }

        Type rhsType = optRHSType.get();

        boolean isBigDecimal = BigDecimal.class.equals(rhsType);
        if(isBigDecimal) {
            if(AssignExpr.Operator.PLUS.equals(n.getOperator())) {
                return new ConvertPlus(rhs);
            } else if(AssignExpr.Operator.MINUS.equals(n.getOperator())) {
                return new ConvertMinus(rhs);
            } else {
                return new DoNotConvert(rhs);
            }
        }
        return new DoNotConvert(null);
    }

    public abstract boolean shouldConvert();

    protected abstract TypedExpression convertPlusEqualsOperatorBigDecimal(TypedExpression target);

    public TypedExpression convertExpression(TypedExpression target) {
        TypedExpression arithmeticExpression = convertPlusEqualsOperatorBigDecimal(target);
        return new AssignExprT(AssignExpr.Operator.ASSIGN, target, arithmeticExpression);
    }

    static class ConvertPlus extends BigDecimalConversion {

        ConvertPlus(TypedExpression rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return true;
        }

        @Override
        public TypedExpression convertPlusEqualsOperatorBigDecimal(TypedExpression target) {
            return new MethodCallExprT("add", of(target), singletonList(rhs), of(BigDecimal.class));
        }
    }

    static class ConvertMinus extends BigDecimalConversion {

        ConvertMinus(TypedExpression rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return true;
        }

        @Override
        public TypedExpression convertPlusEqualsOperatorBigDecimal(TypedExpression target) {
            return new MethodCallExprT("subtract", of(target), singletonList(rhs), of(BigDecimal.class));
        }
    }

    static class DoNotConvert extends BigDecimalConversion {

        DoNotConvert(TypedExpression rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return false;
        }

        @Override
        public TypedExpression convertPlusEqualsOperatorBigDecimal(TypedExpression target) {
            throw new UnsupportedOperationException();
        }
    }
}



