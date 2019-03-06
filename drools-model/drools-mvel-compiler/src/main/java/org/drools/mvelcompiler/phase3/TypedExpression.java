package org.drools.mvelcompiler.phase3;

import java.lang.reflect.Type;

import com.github.javaparser.ast.expr.Expression;

public interface TypedExpression {

    Type getType();

    Expression toJavaExpression();
}

