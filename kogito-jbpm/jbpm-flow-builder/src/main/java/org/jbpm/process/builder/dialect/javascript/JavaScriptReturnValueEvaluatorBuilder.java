package org.jbpm.process.builder.dialect.javascript;

import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.JavaScriptReturnValueEvaluator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;

public class JavaScriptReturnValueEvaluatorBuilder implements ReturnValueEvaluatorBuilder {

    public JavaScriptReturnValueEvaluatorBuilder() {

    }

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator constraintNode,
                      final ReturnValueDescr descr,
                      final ContextResolver contextResolver) {
        String text = descr.getText();
        JavaScriptReturnValueEvaluator expr = new JavaScriptReturnValueEvaluator(text);
        constraintNode.setEvaluator(expr);
    }

}
