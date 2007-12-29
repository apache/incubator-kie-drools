package org.drools.process.builder;

import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.core.Node;


public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
