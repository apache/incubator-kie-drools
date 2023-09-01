package org.drools.verifier.core.index.keys;

import java.util.ArrayList;

import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.RetractHandler;
import org.drools.verifier.core.maps.util.HasKeys;

public class UUIDKey
        extends Key {

    public static final KeyDefinition UNIQUE_UUID = KeyDefinition.newKeyDefinition().withId("unique---uuid").build();

    private ArrayList<RetractHandler> retractHandlers = new ArrayList<>();

    private HasKeys hasKeys;

    UUIDKey(final HasKeys hasKeys,
            final String uuid) {
        super(UNIQUE_UUID,
              uuid);
        this.hasKeys = hasKeys;
    }

    public static UUIDKey getUUIDKey(final Key[] keys) {
        UUIDKey result = null;
        for (final Key key : keys) {
            if (key instanceof UUIDKey) {

                if (result == null) {
                    result = (UUIDKey) key;
                } else {
                    throw new IllegalArgumentException("You can only have one uuid key.");
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("You must set a uuid key.");
        } else {
            return result;
        }
    }

    public void retract() {
        for (final RetractHandler retractHandler : retractHandlers) {
            retractHandler.retract(this);
        }
    }

    public void addRetractHandler(final RetractHandler retractHandler) {
        retractHandlers.add(retractHandler);
    }

    public Key[] getKeys() {
        return hasKeys.keys();
    }

    @Override
    public int compareTo(final Key key) {
        return getSingleValue().compareTo(key.getSingleValue());
    }
}
