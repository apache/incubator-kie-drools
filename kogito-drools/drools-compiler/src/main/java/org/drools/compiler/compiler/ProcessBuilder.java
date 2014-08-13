package org.drools.compiler.compiler;

import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ProcessBuilder {

    List<Process> addProcessFromXml(Resource resource) throws IOException;

    List<BaseKnowledgeBuilderResultImpl> getErrors();
}
