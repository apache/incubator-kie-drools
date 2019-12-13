package org.drools.mvelcompiler.context;

import java.util.Optional;

import org.drools.mvelcompiler.MvelCompilerException;
import org.drools.core.addon.TypeResolver;

public class MvelCompilerContext {

    private ContextDeclarations contextDeclarations = new ContextDeclarations();
    private final TypeResolver typeResolver;

    public MvelCompilerContext(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public MvelCompilerContext addDeclaration(String name, Class<?> clazz) {
        contextDeclarations.addDeclarations(new Declaration(name, clazz));
        return this;
    }

    public MvelCompilerContext addCreatedDeclaration(String name, Class<?> clazz) {
        contextDeclarations.addDeclarations(new Declaration(name, clazz, true));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        return contextDeclarations.findDeclaration(name);
    }

    public Optional<Class<?>> findEnum(String name) {
        try {
            return Optional.of(typeResolver.resolveType(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public Class<?> resolveType(String name) {
        try {
            return typeResolver.resolveType(name);
        } catch (ClassNotFoundException e) {
            throw new MvelCompilerException(e);
        }
    }
}
