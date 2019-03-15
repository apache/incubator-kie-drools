package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public abstract class TypedExpression {

    Node originalExpression;
    List<TypedExpression> children = new ArrayList<>();

    TypedExpression(Node originalExpression) {
        this.originalExpression = originalExpression;
    }

    public TypedExpression addChildren(TypedExpression te) {
        children.add(te);
        return this;
    }

    public abstract Optional<Type> getType();

    public Node toJavaExpression() {
        if (!children.isEmpty()) {
            TypedExpression last = children.get(children.size() - 1);
            return last.toJavaExpression();
        } else {
            return toJavaExpression();
        }
    }

    @Override
    public String toString() {
        return "TypedExpression{" +
                "originalExpression=" + printConstraint(originalExpression) +
                ", children=" + children.stream().map(Object::toString).collect(Collectors.joining()) +
                '}';
    }
}

