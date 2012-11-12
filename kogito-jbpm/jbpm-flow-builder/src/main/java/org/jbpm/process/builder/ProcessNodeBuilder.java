package org.jbpm.process.builder;

import org.drools.lang.descr.ProcessDescr;
import org.kie.definition.process.Node;
import org.kie.definition.process.Process;

public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
