package org.kie.builder;

public interface InternalKieBuilder extends KieBuilder {
    KieBuilderSet createFileSet(String... files);
}
