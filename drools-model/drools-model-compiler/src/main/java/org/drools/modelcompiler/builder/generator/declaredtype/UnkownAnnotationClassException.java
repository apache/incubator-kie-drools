package org.drools.modelcompiler.builder.generator.declaredtype;

public class UnkownAnnotationClassException extends RuntimeException {

    private String name;

    public UnkownAnnotationClassException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
