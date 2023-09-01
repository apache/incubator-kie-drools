package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.expr.LiteralExpr;

public abstract class TemporalChunkExpr extends LiteralExpr {

    public TemporalChunkExpr() {
    }

    public TemporalChunkExpr(TokenRange tokenRange) {
        super(tokenRange);
    }
}
