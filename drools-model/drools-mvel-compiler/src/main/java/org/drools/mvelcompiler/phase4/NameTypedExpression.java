package org.drools.mvelcompiler.phase4;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class NameTypedExpression implements TypedExpression {

    final Node expression;
    final Type type;

    public NameTypedExpression(Node expression, Type type) {
        this.expression = expression;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(printConstraint(expression));
    }

    @Override
    public Stream<Node> stream() {
        Stream.Builder<Node> builder = Stream.builder();
//        builder.add(expression);
        addParentNode(builder, expression);
        return builder.build();
    }

    public void addParentNode(Stream.Builder<Node> aggr, Node n) {
        n.getParentNode().ifPresent(pn -> {
            aggr.add(n);
            addParentNode(aggr, pn);
        });
    }

    @Override
    public String toString() {
        return "NameTypedExpression{" +
                "expression=" + printConstraint(expression) +
                ", type=" + type +
                '}';
    }
}
