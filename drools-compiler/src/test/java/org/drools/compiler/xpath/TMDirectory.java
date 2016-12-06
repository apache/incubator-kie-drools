package org.drools.compiler.xpath;

import java.util.List;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveList;

public class TMDirectory extends AbstractReactiveObject {
    private final String name;
    private final List<TMFile> members = new ReactiveList<TMFile>();

    public TMDirectory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<TMFile> getFiles() {
        return members;
    }
}
