package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

public class MapCreationLiteralExpressionKeyValuePair extends Expression {

    private final Expression key;
    private final Expression value;

    public MapCreationLiteralExpressionKeyValuePair(TokenRange tokenRange, Expression key, Expression value) {
        super(tokenRange);
        this.key = key;
        this.value = value;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>) v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>) v).visit(this, arg);
    }

    public Expression getKey() {
        return key;
    }

    public Expression getValue() {
        return value;
    }
}
