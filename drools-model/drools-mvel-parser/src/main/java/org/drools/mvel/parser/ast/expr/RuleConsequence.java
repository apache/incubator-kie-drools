package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class RuleConsequence extends RuleItem {

    private final Statement statement;

    public RuleConsequence(TokenRange range, Statement statement) {
        super( range );
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
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
