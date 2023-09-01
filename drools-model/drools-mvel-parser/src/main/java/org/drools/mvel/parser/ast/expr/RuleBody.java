package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class RuleBody extends Node {

    private final NodeList<RuleItem> items;

    public RuleBody(TokenRange range, NodeList<RuleItem> items ) {
        super( range );
        this.items = items;
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
