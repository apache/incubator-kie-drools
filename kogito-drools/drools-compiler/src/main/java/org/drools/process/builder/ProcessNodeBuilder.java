package org.drools.process.builder;

import org.drools.knowledge.definitions.process.Node;
import org.drools.knowledge.definitions.process.Process;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;


public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
