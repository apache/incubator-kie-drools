package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;

public class PointFreeExpr extends Expression {

    private final Expression left;
    private final NodeList<Expression> right;

    private final SimpleName operator;
    private boolean negated;
    private final Expression arg1;
    private final Expression arg2;
    private final Expression arg3;
    private final Expression arg4;

    public PointFreeExpr( TokenRange tokenRange, Expression left, NodeList<Expression> right, SimpleName operator, Boolean negated, Expression arg1, Expression arg2, Expression arg3, Expression arg4 ) {
        super(tokenRange);
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.negated = negated;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>)v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>)v).visit(this, arg);
    }

    public Expression getLeft() {
        return left;
    }

    public NodeList<Expression> getRight() {
        return right;
    }

    public SimpleName getOperator() {
        return operator;
    }

    public boolean isNegated() {
        return negated;
    }

    public Expression getArg1() {
        return arg1;
    }

    public Expression getArg2() {
        return arg2;
    }

    public Expression getArg3() {
        return arg3;
    }

    public Expression getArg4() {
        return arg4;
    }
}
