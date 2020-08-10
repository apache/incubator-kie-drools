package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

public class BinaryTExpr implements TypedExpression {

    private static final List<Type> PRIORITIZED_TYPES = Arrays.asList( String.class,
            BigDecimal.class, BigInteger.class,
            Double.class, double.class,
            Long.class, long.class,
            Float.class, Float.class,
            Integer.class, int.class );

    private final TypedExpression left;
    private final TypedExpression right;
    private final BinaryExpr.Operator operator;

    public BinaryTExpr(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression getRight() {
        return right;
    }

    public BinaryExpr.Operator getOperator() {
        return operator;
    }

    @Override
    public Optional<Type> getType() {
        Optional<Type> leftType = left.getType();
        Optional<Type> rightType = right.getType();
        if (!leftType.isPresent()) {
            return rightType;
        }
        if (!rightType.isPresent()) {
            return leftType;
        }
        return Optional.of( combine(leftType.get(), rightType.get()) );
    }

    @Override
    public Node toJavaExpression() {
        return new BinaryExpr((Expression) left.toJavaExpression(), (Expression) right.toJavaExpression(), operator);
    }

    private static Type combine(Type t1, Type t2) {
        for (Type t : PRIORITIZED_TYPES) {
            if (t1 == t || t2 == t) {
                return t;
            }
        }
        return t2;
    }


}
