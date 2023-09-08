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

import java.util.Iterator;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.matchers.ComparableMatchers;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public abstract class Condition<T extends Comparable>
        implements HasKeys {

    private final static KeyDefinition SUPER_TYPE = KeyDefinition.newKeyDefinition()
            .withId("superType")
            .updatable()
            .build();
    private final static KeyDefinition COLUMN_UUID = KeyDefinition.newKeyDefinition()
            .withId("columnUUID")
            .build();
    private final static KeyDefinition VALUE = KeyDefinition.newKeyDefinition()
            .withId("value")
            .updatable()
            .build();

    protected final UUIDKey uuidKey;
    protected final Column column;
    private final ConditionSuperType superType;
    private final Values<Comparable> values = new Values<>();
    private UpdatableKey<Condition<T>> valueKey;

    public Condition(final Column column,
                     final ConditionSuperType superType,
                     final Values<T> values,
                     final AnalyzerConfiguration configuration) {
        PortablePreconditions.checkNotNull("values",
                                           values);
        PortablePreconditions.checkNotNull("configuration",
                                           configuration);

        this.column = PortablePreconditions.checkNotNull("column",
                                                         column);
        this.superType = PortablePreconditions.checkNotNull("superType",
                                                            superType);
        this.uuidKey = configuration.getUUID(this);
        this.valueKey = new UpdatableKey<>(VALUE,
                                           values);
        resetValues();
    }

    public static ComparableMatchers value() {
        return new ComparableMatchers(VALUE);
    }

    public static Matchers columnUUID() {
        return new Matchers(COLUMN_UUID);
    }

    public static Matchers superType() {
        return new Matchers(SUPER_TYPE);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                VALUE,
                SUPER_TYPE,
                COLUMN_UUID
        };
    }

    private void resetValues() {
        values.clear();

        for (final Value o : valueKey.getValues()) {
            values.add(o.getComparable());
        }
    }

    public Column getColumn() {
        return column;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public T getFirstValue() {
        final Iterator<Value> iterator = valueKey.getValues()
                .iterator();
        if (iterator.hasNext()) {
            return (T) iterator.next()
                    .getComparable();
        } else {
            return null;
        }
    }

    public Values<Comparable> getValues() {
        return values;
    }

    public void setValue(final Values<T> values) {
        if (!valueKey.getValues()
                .isThereChanges(values)) {
            return;
        } else {
            final UpdatableKey<Condition<T>> oldKey = valueKey;

            final UpdatableKey<Condition<T>> newKey = new UpdatableKey<>(VALUE,
                                                                         values);

            valueKey = newKey;

            oldKey.update(newKey,
                          this);
            resetValues();
        }
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                valueKey,
                new Key(SUPER_TYPE,
                        superType),
                new Key(COLUMN_UUID,
                        column.getUuidKey()),
        };
    }
}
