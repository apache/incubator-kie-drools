package org.drools.compiler.compiler;

import java.io.IOException;
import java.util.List;

import org.kie.api.io.Resource;

public interface ProcessBuilder {

    List<BaseKnowledgeBuilderResultImpl> addProcessFromXml(Resource resource) throws IOException;

    List<BaseKnowledgeBuilderResultImpl> getErrors();
}
