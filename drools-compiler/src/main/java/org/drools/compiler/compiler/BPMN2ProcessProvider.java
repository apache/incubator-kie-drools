package org.drools.compiler.compiler;

import org.kie.api.Service;
import org.kie.internal.builder.KnowledgeBuilder;

public interface BPMN2ProcessProvider extends Service {

    void configurePackageBuilder(KnowledgeBuilder packageBuilder);

}
