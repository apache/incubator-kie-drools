package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.Optional;

@FunctionalInterface
public interface TypeResolver {
    Optional<Class<?>> resolveType(String className );
}
