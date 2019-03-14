package org.drools.mvelcompiler.context;

import java.util.Optional;

import org.drools.mvelcompiler.MvelCompilerException;

public class MvelCompilerContext {
    public Declarations declarations = new Declarations();

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
}
