package org.jbpm.process.builder.dialect.javascript;

import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.JavaScriptAction;
import org.jbpm.workflow.core.DroolsAction;

public class JavaScriptActionBuilder implements ActionBuilder {

    public JavaScriptActionBuilder() {
    }

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver) {
        String text = actionDescr.getText();
        JavaScriptAction expr = new JavaScriptAction(text);
        action.setMetaData("Action",  expr );
    }

}
