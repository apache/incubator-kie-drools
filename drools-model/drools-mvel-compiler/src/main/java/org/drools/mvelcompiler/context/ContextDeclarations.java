package org.drools.mvelcompiler.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContextDeclarations {
    private Map<String, Declaration> declarations = new HashMap<>();

    void addDeclarations(Declaration d) {
        declarations.put(d.getName(), d);
    }

    Optional<Declaration> findDeclaration(String name) {
        return Optional.ofNullable(declarations.get(name));
    }
}
