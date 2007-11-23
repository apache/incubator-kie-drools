package org.drools.rule.builder;

import org.drools.lang.descr.ActionDescr;
import org.drools.ruleflow.core.impl.ActionNodeImpl;

public interface ActionBuilder {

    public void build(final PackageBuildContext context,
                      final ActionNodeImpl actionNode,
                      final ActionDescr actionDescr);

}