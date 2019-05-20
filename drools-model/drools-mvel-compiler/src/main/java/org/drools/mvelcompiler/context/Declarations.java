package org.drools.mvelcompiler.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Declarations {
    public Map<String, Declaration> declarations = new HashMap<>();

    public void addDeclarations(Declaration d) {
        declarations.put(d.getName(), d);
    }

    public Optional<Declaration> findDeclaration(String name) {
        return Optional.ofNullable(declarations.get(name));
    }

    public Collection<Declaration> getCreatedDeclarsations() {
        return declarations.values().stream().filter(Declaration::getCreated).collect(Collectors.toList());
    }
}
