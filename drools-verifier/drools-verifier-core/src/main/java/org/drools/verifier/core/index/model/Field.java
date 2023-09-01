package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.util.PortablePreconditions;

public class Field
        extends FieldBase {

    private final ObjectField objectField;
    private final Conditions conditions = new Conditions();
    private final Actions actions = new Actions();

    public Field(final ObjectField objectField,
                 final String factType,
                 final String fieldType,
                 final String name,
                 final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
        this.objectField = PortablePreconditions.checkNotNull("objectField",
                                                              objectField);
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Actions getActions() {
        return actions;
    }

    public void remove(final Column column) {
        this.conditions.remove(column);
        this.actions.remove(column);
    }
}
