package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.maps.KeyDefinition;

public class FieldMatchers
        extends KeyMatcher {

    private String factType;

    public FieldMatchers(final KeyDefinition keyDefinition) {
        super(keyDefinition);
    }

    public FieldName factType(final String factType) {
        this.factType = factType;
        return new FieldName();
    }

    public class FieldName {

        public Matcher fieldName(final String fieldName) {
            return new ExactMatcher(keyDefinition,
                                    factType + "." + fieldName);
        }
    }
}
