package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

public class FullyQualifiedInlineCastExpr extends Expression {

    private Expression scope;
    private Name name;
    private NodeList<Expression> arguments;

    public FullyQualifiedInlineCastExpr( Expression scope, Name name ) {
        this( scope, name, null );
    }

    public FullyQualifiedInlineCastExpr( Expression scope, Name name, NodeList<Expression> arguments ) {
        this( null, scope, name, arguments );
    }

    public FullyQualifiedInlineCastExpr( TokenRange tokenRange, Expression scope, Name name ) {
        this( tokenRange, scope, name, null );
    }

    public FullyQualifiedInlineCastExpr( TokenRange tokenRange, Expression scope, Name name, NodeList<Expression> arguments ) {
        super( tokenRange );
        this.scope = scope;
        this.name = name;
        this.arguments = arguments;
    }

    public boolean hasArguments() {
        return arguments != null;
    }

    public Expression getScope() {
        return scope;
    }

    public Name getName() {
        return name;
    }

    public NodeList<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return (( DrlGenericVisitor<R, A> )v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        (( DrlVoidVisitor<A> )v).visit(this, arg);
    }
}
