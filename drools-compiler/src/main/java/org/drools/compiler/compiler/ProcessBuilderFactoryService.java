package org.drools.compiler.compiler;

import org.kie.api.internal.utils.KieService;
import org.kie.internal.builder.KnowledgeBuilder;

/**
 * ProcessBuilderFactoryService is used by the PackageBuilder to "provide" it's concrete implementation.
 */
public interface ProcessBuilderFactoryService extends KieService {

    ProcessBuilder newProcessBuilder(KnowledgeBuilder kBuilder);

}
