package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;

public interface TypedExpression {

    Optional<Type> getType();

    Node toJavaExpression();
}

