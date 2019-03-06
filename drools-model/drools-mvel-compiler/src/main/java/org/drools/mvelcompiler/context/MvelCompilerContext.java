package org.drools.mvelcompiler.context;

import java.util.Optional;

public class MvelCompilerContext {
    public Declarations declarations = new Declarations();

    public MvelCompilerContext addDeclaration(String name, Class<?> clazz) {
        declarations.addDeclarations(new Declaration(name, clazz));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        return declarations.findDeclaration(name);
    }

    public void addCompilationError(String message) {

    }
}
