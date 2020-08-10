package org.drools.mvelcompiler.bigdecimal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.BinaryTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.ObjectCreationExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;

import static org.drools.mvel.parser.MvelParser.parseClassOrInterfaceType;

public abstract class BigDecimalConversion<T extends TypedExpression> {
    T rhs;

    BigDecimalConversion(T rhs) {
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

        boolean isBigDecimal = BigDecimal.class == rhsType;
        if (isBigDecimal) {
            if (AssignExpr.Operator.PLUS == n.getOperator()) {
                return new ConvertPlus(rhs);
            }
            if (AssignExpr.Operator.MINUS == n.getOperator()) {
                return new ConvertMinus(rhs);
            }
            if (rhs instanceof BinaryTExpr) {
                BinaryExpr.Operator binOp = (( BinaryTExpr ) rhs).getOperator();
                if (binOp == BinaryExpr.Operator.PLUS) {
                    return new ConvertBinaryPlus((BinaryTExpr) rhs);
                }
                if (binOp == BinaryExpr.Operator.MINUS) {
                    return new ConvertBinaryMinus((BinaryTExpr) rhs);
                }
            }
        }
        return new DoNotConvert(null);
    }

    public abstract boolean shouldConvert();

    public abstract TypedExpression convertExpression(TypedExpression target);

    protected TypedExpression convertExpression(TypedExpression target, TypedExpression leftExpr, TypedExpression rightExpr, String op) {
        Optional<Type> rightType = rightExpr.getType();
        if (rightType.isPresent() && rightType.get() != BigDecimal.class) {
            ObjectCreationExpr creationExpr = new ObjectCreationExpr(null, parseClassOrInterfaceType(BigDecimal.class.getCanonicalName()), NodeList.nodeList(( Expression ) rightExpr.toJavaExpression()));
            rightExpr = new ObjectCreationExpressionT( creationExpr, BigDecimal.class);
        }
        TypedExpression arithmeticExpression = new MethodCallExprT(op, of(leftExpr), singletonList(rightExpr), of(BigDecimal.class));
        if (target instanceof FieldToAccessorTExpr ) {
            return (( FieldToAccessorTExpr ) target).withArguments( singletonList(arithmeticExpression) );
        }
        return new AssignExprT(AssignExpr.Operator.ASSIGN, target, arithmeticExpression);
    }

    static class ConvertPlus extends BigDecimalConversion<TypedExpression> {

        ConvertPlus(TypedExpression rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return true;
        }

        @Override
        public TypedExpression convertExpression(TypedExpression target) {
            return convertExpression(target, target, rhs, "add");
        }
    }

    static class ConvertBinaryPlus extends BigDecimalConversion<BinaryTExpr> {

        ConvertBinaryPlus(BinaryTExpr rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return true;
        }

        @Override
        public TypedExpression convertExpression(TypedExpression target) {
            return convertExpression(target, rhs.getLeft(), rhs.getRight(), "add");
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
        public TypedExpression convertExpression(TypedExpression target) {
            return convertExpression(target, target, rhs, "subtract");
        }
    }

    static class ConvertBinaryMinus extends BigDecimalConversion<BinaryTExpr> {

        ConvertBinaryMinus(BinaryTExpr rhs) {
            super(rhs);
        }

        @Override
        public boolean shouldConvert() {
            return true;
        }

        @Override
        public TypedExpression convertExpression(TypedExpression target) {
            return convertExpression(target, rhs.getLeft(), rhs.getRight(), "subtract");
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
        public TypedExpression convertExpression(TypedExpression target) {
            throw new UnsupportedOperationException();
        }
    }
}



