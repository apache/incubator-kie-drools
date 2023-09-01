package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;

public class ForStmtT implements TypedExpression {

    private final List<TypedExpression> initialization;
    private final Optional<TypedExpression> compare;
    private final List<TypedExpression> update;
    private TypedExpression body;

    public ForStmtT(List<TypedExpression> initialization, Optional<TypedExpression> compare, List<TypedExpression> update, TypedExpression body) {
        this.initialization = initialization;
        this.compare = compare;
        this.update = update;
        this.body = body;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        ForStmt stmt = new ForStmt();
        stmt.setInitialization(NodeList.nodeList(initialization.stream().map(TypedExpression::toJavaExpression)
                .map(Expression.class::cast)
                .collect(Collectors.toList())));

        compare.ifPresent(c -> stmt.setCompare((Expression) c.toJavaExpression()));

        stmt.setUpdate(NodeList.nodeList(update.stream().map(TypedExpression::toJavaExpression)
                .map(Expression.class::cast)
                .collect(Collectors.toList())));

        stmt.setBody((Statement) body.toJavaExpression());
        return stmt;
    }
}
