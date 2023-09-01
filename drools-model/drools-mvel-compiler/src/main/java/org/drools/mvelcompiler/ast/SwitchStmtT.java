package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;

public class SwitchStmtT implements TypedExpression {

    private final TypedExpression selector;
    private final List<TypedExpression> entries;

    public SwitchStmtT(TypedExpression selector, List<TypedExpression> entries) {
        this.selector = selector;
        this.entries = entries;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        SwitchStmt stmt = new SwitchStmt();
        stmt.setSelector((Expression) selector.toJavaExpression());

        stmt.setEntries(NodeList.nodeList(entries.stream().map(TypedExpression::toJavaExpression)
                                                 .map(SwitchEntry.class::cast)
                                                 .collect(Collectors.toList())));

        return stmt;
    }
}
