package org.drools.constraint.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.constraint.parser.ast.visitor.DrlVoidVisitor;

public class ModifyExpression extends Expression {

    private final SimpleName modifyObject;
    private final NodeList<Expression> expressions;

    public ModifyExpression(TokenRange tokenRange, SimpleName modifyObject, NodeList<Expression> expressions) {
        super(tokenRange);
        this.modifyObject = modifyObject;
        this.expressions = expressions;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>)v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>)v).visit(this, arg);
    }

    public NodeList<Expression> getExpressions() {
        return expressions;
    }

    public SimpleName getModifyObject() {
        return modifyObject;
    }
}
