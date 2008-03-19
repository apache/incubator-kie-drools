package org.drools.rule.builder;

import org.drools.compiler.ReturnValueDescr;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;

public interface ReturnValueEvaluatorBuilder {

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
                      final ReturnValueDescr returnValueDescr);

}