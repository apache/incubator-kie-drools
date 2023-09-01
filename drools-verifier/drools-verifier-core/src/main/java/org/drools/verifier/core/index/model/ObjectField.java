package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class ObjectField
        extends FieldBase {

    public ObjectField(final String factType,
                       final String fieldType,
                       final String name,
                       final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
    }
}
