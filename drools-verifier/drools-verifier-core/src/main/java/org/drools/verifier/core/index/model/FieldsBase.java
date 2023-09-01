package org.drools.verifier.core.index.model;

import java.util.Collection;

import org.drools.verifier.core.maps.KeyTreeMap;

public class FieldsBase<T extends FieldBase> {

    public final KeyTreeMap<T> map = new KeyTreeMap<>(Field.keyDefinitions());

    public FieldsBase() {

    }

    public void merge(final FieldsBase fields) {
        map.merge(fields.map);
    }

    public FieldsBase(final Collection<T> fields) {
        for (final T field : fields) {
            add(field);
        }
    }

    public void add(final T field) {
        map.put(field);
    }
}
