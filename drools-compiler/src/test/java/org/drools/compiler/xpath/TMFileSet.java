package org.drools.compiler.xpath;

import java.util.Set;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveSet;

public class TMFileSet extends AbstractReactiveObject {
    private final String name;
    private final Set<TMFile> members = new ReactiveSet<TMFile>();

    public TMFileSet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<TMFile> getFiles() {
        return members;
    }
}
