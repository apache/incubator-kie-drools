package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Optional;

@FunctionalInterface
public interface TypeResolver {
    Optional<Class<?>> resolveType(String className );
}
