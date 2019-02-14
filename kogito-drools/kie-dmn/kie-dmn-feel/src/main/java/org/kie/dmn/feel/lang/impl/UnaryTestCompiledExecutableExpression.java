package org.kie.dmn.feel.lang.impl;

import java.util.List;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;

public class UnaryTestCompiledExecutableExpression {

    private final CompiledFEELUnaryTests expr;

    public UnaryTestCompiledExecutableExpression(CompiledFEELUnaryTests expr) {
        this.expr = expr;
    }

    public List<UnaryTest> getUnaryTests() {
        return expr.getUnaryTests();
    }
}
