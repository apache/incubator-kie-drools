package org.kie.internal.builder;

import java.util.function.Function;

import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.ResourceType;

public interface AssemblerContext {

    <T extends ResourceTypePackage<?>> T computeIfAbsent(
            ResourceType resourceType,
            String namespace,
            Function<? super ResourceType, T> mappingFunction);

    void reportError(KnowledgeBuilderError error);
}
