package org.drools.process.builder;

import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;


public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
