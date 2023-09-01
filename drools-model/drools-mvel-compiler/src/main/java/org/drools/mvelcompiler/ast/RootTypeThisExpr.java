package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;

public class RootTypeThisExpr implements TypedExpression {

    private final Type type;
    private String rootTypePrefix;

    public RootTypeThisExpr(Type type, String rootTypePrefix) {
        this.type = type;
        this.rootTypePrefix = rootTypePrefix;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        return new NameExpr(rootTypePrefix);
    }
}
