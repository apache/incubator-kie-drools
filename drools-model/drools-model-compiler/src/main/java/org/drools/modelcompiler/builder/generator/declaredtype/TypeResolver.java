package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Optional;

public interface TypeResolver {
    Optional<Class<?>> resolveType(String className );
}
