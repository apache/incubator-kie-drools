package org.kie.internal.builder;

import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;

public interface InternalKieBuilder extends KieBuilder {

    KieBuilderSet createFileSet(String... files);

    KieModule getKieModuleIgnoringErrors();
}
