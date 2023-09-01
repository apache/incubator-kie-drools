package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.expr.SimpleName;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class RulePattern extends RuleItem {

    private final SimpleName type;
    private final SimpleName bind;
    private final OOPathExpr expr;

    public RulePattern(TokenRange range, SimpleName type, SimpleName bind, OOPathExpr expr ) {
        super( range );
        this.type = type;
        this.bind = bind;
        this.expr = expr;
    }

    public SimpleName getType() {
        return type;
    }

    public SimpleName getBind() {
        return bind;
    }

    public OOPathExpr getExpr() {
        return expr;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>)v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>)v).visit(this, arg);
    }

}
