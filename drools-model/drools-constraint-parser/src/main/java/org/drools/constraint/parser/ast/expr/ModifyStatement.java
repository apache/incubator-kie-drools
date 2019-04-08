package org.drools.constraint.parser.ast.expr;

import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.constraint.parser.ast.visitor.DrlVoidVisitor;

public class ModifyStatement extends Statement {

    private final SimpleName modifyObject;
    private final NodeList<Statement> expressions;

    public ModifyStatement(TokenRange tokenRange, SimpleName modifyObject, NodeList<Statement> expressions) {
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

    public NodeList<Statement> getExpressions() {
        return expressions;
    }

    public SimpleName getModifyObject() {
        return modifyObject;
    }

    @Override
    public List<Node> getChildNodes() {
        NodeList nodeList = NodeList.nodeList();
        for(Statement e : expressions) {
            nodeList.add(e);
        }
        return nodeList;
    }
}
