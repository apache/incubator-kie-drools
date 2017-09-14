package org.drools.model;

public interface Rule {

    enum Attribute {
        SALIENCE(0),
        NO_LOOP(false),
        AGENDA_GROUP(null);

        private final Object defaultValue;

        Attribute(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    View getView();

    Consequence getConsequence();

    Object getAttribute(Attribute attribute);

    String getName();
    String getPackage();
    String getUnit();
}
