package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;

public class ObjectType
        implements HasKeys {

    private final static KeyDefinition TYPE = KeyDefinition.newKeyDefinition()
            .withId("type")
            .build();

    private final UUIDKey uuidKey;
    private final String type;
    private final ObjectFields fields = new ObjectFields();

    public ObjectType(final String type,
                      final AnalyzerConfiguration configuration) {
        this.type = type;
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers type() {
        return new Matchers(TYPE);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                TYPE
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getType() {
        return type;
    }

    public ObjectFields getFields() {
        return fields;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(TYPE,
                        type)
        };
    }
}
