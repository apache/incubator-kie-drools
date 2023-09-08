/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public abstract class Action
        implements HasKeys {

    protected static final KeyDefinition VALUE = KeyDefinition.newKeyDefinition()
            .withId("value")
            .updatable()
            .build();
    protected static final KeyDefinition SUPER_TYPE = KeyDefinition.newKeyDefinition()
            .withId("superType")
            .updatable()
            .build();
    protected static final KeyDefinition COLUMN_UUID = KeyDefinition.newKeyDefinition()
            .withId("columnUUID")
            .build();

    protected final UUIDKey uuidKey;
    protected final Column column;
    private final ActionSuperType superType;
    private final Values<Comparable> values = new Values<>();
    protected UpdatableKey<Action> valueKey;

    public Action(final Column column,
                  final ActionSuperType superType,
                  final Values values,
                  final AnalyzerConfiguration configuration) {
        this.column = PortablePreconditions.checkNotNull("column",
                                                         column);
        this.superType = PortablePreconditions.checkNotNull("superType",
                                                            superType);
        this.valueKey = new UpdatableKey<>(Action.VALUE,
                                           values);
        this.uuidKey = configuration.getUUID(this);
        resetValues();
    }

    public static Matchers value() {
        return new Matchers(VALUE);
    }

    public static Matchers superType() {
        return new Matchers(SUPER_TYPE);
    }

    public static Matchers columnUUID() {
        return new Matchers(COLUMN_UUID);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                COLUMN_UUID,
                SUPER_TYPE,
                VALUE
        };
    }

    private void resetValues() {
        values.clear();

        for (final Value o : valueKey.getValues()) {
            values.add(o.getComparable());
        }
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public Values<Comparable> getValues() {
        return values;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(SUPER_TYPE,
                        superType),
                new Key(COLUMN_UUID,
                        column.getUuidKey()),
                valueKey
        };
    }

    public void setValue(final Values values) {
        if (!valueKey.getValues()
                .isThereChanges(values)) {
            return;
        } else {
            final UpdatableKey<Action> oldKey = valueKey;

            final UpdatableKey<Action> newKey = new UpdatableKey<>(Action.VALUE,
                                                                   values);
            valueKey = newKey;

            oldKey.update(newKey,
                          this);
            resetValues();
        }
    }
}
