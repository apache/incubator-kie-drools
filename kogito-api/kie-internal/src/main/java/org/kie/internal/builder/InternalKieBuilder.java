package org.kie.internal.builder;

import org.kie.api.builder.KieBuilder;

public interface InternalKieBuilder extends KieBuilder {
    KieBuilderSet createFileSet(String... files);
}
