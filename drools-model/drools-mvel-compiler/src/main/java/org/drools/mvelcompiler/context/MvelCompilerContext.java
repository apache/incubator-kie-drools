package org.drools.mvelcompiler.context;

import java.util.Optional;

import org.drools.mvelcompiler.MvelCompilerException;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

public class MvelCompilerContext {

    private Declarations declarations = new Declarations();
    private final TypeResolver typeResolver;

    public MvelCompilerContext(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public MvelCompilerContext addDeclaration(String name, Class<?> clazz) {
        declarations.addDeclarations(new Declaration(name, clazz));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        return declarations.findDeclaration(name);
    }

    public Declaration getDeclarations(String name) {
        return declarations
                .findDeclaration(name)
                .orElseThrow(() -> new MvelCompilerException("No declaration found with name: " + name));
    }

    public Declaration getOrCreateDeclarations(String name, Class<?> clazz) {
        return declarations
                .getOrCreateDeclarations(name, clazz);
    }

    public void addCompilationError(String message) {

    }

    public Class<?> resolveType(String name) {
        try {
            return typeResolver.resolveType(name);
        } catch (ClassNotFoundException e) {
            throw new MvelCompilerException(e);
        }
    }
}
