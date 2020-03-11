package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.List;

public class UnknownKeysInAnnotation extends RuntimeException {

    private List<String> values;

    public UnknownKeysInAnnotation(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}
