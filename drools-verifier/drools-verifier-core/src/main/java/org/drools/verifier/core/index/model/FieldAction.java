package org.drools.verifier.core.index.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.matchers.FieldMatchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;

public class FieldAction
        extends Action {

    private static final KeyDefinition FIELD = KeyDefinition.newKeyDefinition().withId("field").build();
    private static final KeyDefinition FACT_TYPE__FIELD_NAME = KeyDefinition.newKeyDefinition().withId("factType.fieldName").build();

    private final Field field;

    public FieldAction(final Field field,
                       final Column column,
                       final Values values,
                       final AnalyzerConfiguration configuration) {
        super(column,
              ActionSuperType.FIELD_ACTION,
              values,
              configuration);

        this.field = PortablePreconditions.checkNotNull("field",
                                                        field);
    }

    public static FieldMatchers field() {
        return new FieldMatchers(FIELD);
    }

    public Field getField() {
        return field;
    }

    public static KeyDefinition[] keyDefinitions() {
        KeyDefinition[] origKeys = Action.keyDefinitions();
        KeyDefinition[] keys = new KeyDefinition[origKeys.length +2];
        System.arraycopy(origKeys, 0, keys, 0, origKeys.length);
        keys[keys.length-2] = FIELD;
        keys[keys.length-1] = FIELD;

        return keys;
    }

    @Override
    public Key[] keys() {
        final ArrayList<Key> keys = new ArrayList<>();

        keys.addAll(Arrays.asList(super.keys()));

        keys.add(new Key(FIELD,
                         field));
        keys.add(new Key(FACT_TYPE__FIELD_NAME,
                         field.getFactType() + "." + field.getName()));

        return keys.toArray(new Key[keys.size()]);
    }
}
