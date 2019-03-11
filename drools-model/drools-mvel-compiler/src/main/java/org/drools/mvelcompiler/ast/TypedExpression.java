package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

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

    public abstract Type getType();

    public Node toJavaExpression() {
        if (!children.isEmpty()) {
            TypedExpression last = children.get(children.size() - 1);
            return last.toJavaExpression();
        } else {
            return toJavaExpression();
        }
    }
}

