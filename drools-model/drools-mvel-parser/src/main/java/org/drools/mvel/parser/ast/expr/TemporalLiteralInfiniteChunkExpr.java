package org.drools.mvel.parser.ast.expr;

import java.util.concurrent.TimeUnit;

import com.github.javaparser.TokenRange;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class TemporalLiteralInfiniteChunkExpr extends TemporalChunkExpr {

    public TemporalLiteralInfiniteChunkExpr(TokenRange tokenRange, String value) {
        super(tokenRange);
    }

    public TemporalLiteralInfiniteChunkExpr(TokenRange tokenRange, String value, TimeUnit timeUnit) {
        super(tokenRange);
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
