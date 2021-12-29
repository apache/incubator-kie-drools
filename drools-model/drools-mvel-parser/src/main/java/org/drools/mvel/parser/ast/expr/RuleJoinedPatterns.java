package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

public class RuleJoinedPatterns extends RuleItem {
    private final Type type;
    private final NodeList<RuleItem> items;

    public enum Type { AND, OR }
    public RuleJoinedPatterns(TokenRange range, Type type, NodeList<RuleItem> items) {
        super(range);
        this.type = type;
        this.items = items;
    }

    public Type getType() {
        return type;
    }

    public NodeList<RuleItem> getItems() {
        return items;
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
