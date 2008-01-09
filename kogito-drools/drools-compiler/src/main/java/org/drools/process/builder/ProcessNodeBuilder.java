package org.drools.process.builder;

import org.drools.lang.descr.ProcessDescr;
import org.drools.process.core.Process;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.Node;


public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
