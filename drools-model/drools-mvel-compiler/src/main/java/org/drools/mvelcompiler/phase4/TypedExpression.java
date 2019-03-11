package org.drools.mvelcompiler.phase4;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;

public interface TypedExpression {

    Type getType();

    Expression toJavaExpression();

}

