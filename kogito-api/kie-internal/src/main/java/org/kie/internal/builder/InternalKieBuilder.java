package org.kie.internal.builder;

import org.kie.builder.KieBuilder;

public interface InternalKieBuilder extends KieBuilder {
    KieBuilderSet createFileSet(String... files);
}
