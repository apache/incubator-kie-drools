package org.drools.modelcompiler.builder.generator.drlxparse;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.isNumber;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.operatorToName;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.uncastExpr;

abstract class SpecialComparisonCase {

    TypedExpression left;
    TypedExpression right;

    SpecialComparisonCase(TypedExpression left, TypedExpression right) {
        this.left = left;
        this.right = right;
    }

    String getMethodName(BinaryExpr.Operator operator) {
        return "org.drools.modelcompiler.util.EvaluationUtil." + operatorToName(operator);
    }

    abstract MethodCallExpr createCompareMethod(BinaryExpr.Operator operator);

    static SpecialComparisonCase specialComparisonFactory(TypedExpression left, TypedExpression right) {
        if (left.getType() == String.class && right.getType() == String.class) {
            return new StringAsNumber(left, right);
        } else if (isNumber(left) || isNumber(right)) {
            return new NumberComparisonWithoutCast(left, right);
        } else {
            return new PlainEvaluation(left, right);
        }
    }
}

class StringAsNumber extends SpecialComparisonCase {

    StringAsNumber(TypedExpression left, TypedExpression right) {
        super(left, right);
    }

    @Override
    public MethodCallExpr createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator) + "StringsAsNumbers";
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);
        compareMethod.addArgument(uncastExpr(left.getExpression()));
        compareMethod.addArgument(uncastExpr(right.getExpression()));
        return compareMethod;
    }
}

class NumberComparisonWithoutCast extends SpecialComparisonCase {

    NumberComparisonWithoutCast(TypedExpression left, TypedExpression right) {
        super(left, right);
    }

    @Override
    public MethodCallExpr createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator) + "Numbers";
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);
        compareMethod.addArgument(uncastExpr(left.getExpression()));
        compareMethod.addArgument(uncastExpr(right.getExpression()));
        return compareMethod;
    }
}

class PlainEvaluation extends SpecialComparisonCase {

    PlainEvaluation(TypedExpression left, TypedExpression right) {
        super(left, right);
    }

    @Override
    public MethodCallExpr createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator);
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);
        compareMethod.addArgument(uncastExpr(left.getExpression()));
        compareMethod.addArgument(uncastExpr(right.getExpression()));
        return compareMethod;
    }
}
