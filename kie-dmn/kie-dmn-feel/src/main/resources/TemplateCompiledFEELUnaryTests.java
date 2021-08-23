package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;
import org.kie.dmn.feel.lang.EvaluationContext;


import java.util.List;
import java.util.function.BiFunction;

import org.kie.dmn.feel.runtime.UnaryTest;

public class TemplateCompiledFEELUnaryTests implements org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests {


    @Override
    public java.util.List<org.kie.dmn.feel.runtime.UnaryTest> getUnaryTests() {
        return null;
    }

    private static TemplateCompiledFEELUnaryTests INSTANCE;

    public static TemplateCompiledFEELUnaryTests getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateCompiledFEELUnaryTests();
        }
        return INSTANCE;
    }
}
