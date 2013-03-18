package org.jbpm.process.builder;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.drools.compiler.lang.descr.ProcessDescr;

public interface ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node);
}
