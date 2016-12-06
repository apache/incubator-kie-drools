package org.drools.compiler.xpath;

import org.drools.core.phreak.AbstractReactiveObject;

public class TMFile extends AbstractReactiveObject {
    private final String name;
    private int size;

    public TMFile(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        notifyModification();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TMFile [name=").append(name).append(", size=").append(size).append("]");
        return builder.toString();
    }
}
