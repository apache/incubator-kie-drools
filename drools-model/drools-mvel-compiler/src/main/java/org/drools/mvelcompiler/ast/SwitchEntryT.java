package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;

public class SwitchEntryT implements TypedExpression {

    private final NodeList<Expression> labels;
    private final List<TypedExpression> statements;

    public SwitchEntryT(NodeList<Expression> labels, List<TypedExpression> statements) {
        this.labels = labels;
        this.statements = statements;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        SwitchEntry entry = new SwitchEntry();
        entry.setLabels(labels);
        entry.setStatements(NodeList.nodeList(statements.stream().map(TypedExpression::toJavaExpression)
                                                  .map(Statement.class::cast)
                                                  .collect(Collectors.toList())));
        return entry;
    }
}
