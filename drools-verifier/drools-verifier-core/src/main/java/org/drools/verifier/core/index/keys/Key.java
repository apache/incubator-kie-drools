/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.index.keys;

import org.drools.verifier.core.maps.KeyDefinition;

public class Key
        implements Comparable<Key> {

    private final KeyDefinition keyDefinition;

    private Values<Value> values = new Values<>();

    public Key(final KeyDefinition keyDefinition,
               final Comparable value) {
        this.keyDefinition = keyDefinition;

        this.values.add(new Value(value));
    }

    public Key(final KeyDefinition keyDefinition,
               final Values values) {
        this.keyDefinition = keyDefinition;

        for (final Object value : values) {
            try {

                this.values.add(new Value((Comparable) value));
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            }
        }
    }

    public KeyDefinition getKeyDefinition() {
        return keyDefinition;
    }

    public Values<Value> getValues() {
        return values;
    }

    public Comparable getSingleValueComparator() {
        return getSingleValue().getComparable();
    }

    public Value getSingleValue() {
        return (Value) values.iterator().next();
    }

    @Override
    public int compareTo(final Key key) {
        return keyDefinition.compareTo(key.keyDefinition);
    }
}
