package org.kie.dmn.feel.codegen.feel11;

import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.*;

import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;
import org.kie.dmn.feel.lang.EvaluationContext;

public class TemplateCompiledFEELExpression implements org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression {

    @Override
    public Object apply(EvaluationContext feelExprCtx) {
        return null;
    }

    private static TemplateCompiledFEELExpression INSTANCE;

    public static TemplateCompiledFEELExpression getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateCompiledFEELExpression();
        }
        return INSTANCE;
    }

}
