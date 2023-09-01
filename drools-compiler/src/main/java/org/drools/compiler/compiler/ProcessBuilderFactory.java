package org.drools.compiler.compiler;

import org.kie.api.internal.utils.KieService;
import org.kie.internal.builder.KnowledgeBuilder;


public class ProcessBuilderFactory {

    private static ProcessBuilderFactoryService provider = initializeProvider();

    public static ProcessBuilder newProcessBuilder(KnowledgeBuilder kBuilder) {
        if (provider == null) {
            return null;
        } else {
            return provider.newProcessBuilder(kBuilder);
        }
    }

    private static ProcessBuilderFactoryService initializeProvider() {
        return KieService.load( ProcessBuilderFactoryService.class );
    }
}
