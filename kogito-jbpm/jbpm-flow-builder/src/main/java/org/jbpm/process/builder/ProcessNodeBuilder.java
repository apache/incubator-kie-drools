package org.jbpm.process.builder;

import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.lang.descr.ProcessDescr;

public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
