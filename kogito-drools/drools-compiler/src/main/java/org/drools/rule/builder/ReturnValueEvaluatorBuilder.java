package org.drools.rule.builder;

import org.drools.compiler.ReturnValueDescr;
import org.drools.lang.descr.ActionDescr;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;

public interface ReturnValueEvaluatorBuilder {

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
                      final ReturnValueDescr returnValueDescr);

}