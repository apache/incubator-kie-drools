package org.drools.mvelcompiler.context;

import java.util.Collection;
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

    public MvelCompilerContext addCreatedDeclaration(String name, Class<?> clazz) {
        declarations.addDeclarations(new Declaration(name, clazz, true));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        return declarations.findDeclaration(name);
    }

    public Collection<Declaration> getCreatedDeclarsations() {
        return declarations.getCreatedDeclarsations();
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
