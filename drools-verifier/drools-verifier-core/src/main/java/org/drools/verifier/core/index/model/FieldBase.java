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
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.util.PortablePreconditions;

public abstract class FieldBase
        implements Comparable<FieldBase>,
                   HasKeys,
                   HumanReadable {

    private static KeyDefinition NAME = KeyDefinition.newKeyDefinition()
            .withId("name")
            .updatable()
            .build();

    private final UUIDKey uuidKey;
    private final String factType;
    private final String fieldType;
    private final String name;

    public FieldBase(final String factType,
                     final String fieldType,
                     final String name,
                     final AnalyzerConfiguration configuration) {
        this.factType = PortablePreconditions.checkNotNull("factType",
                                                           factType);
        this.fieldType = PortablePreconditions.checkNotNull("fieldType",
                                                            fieldType);
        this.name = PortablePreconditions.checkNotNull("name",
                                                       name);
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers name() {
        return new Matchers(NAME);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                NAME
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getFactType() {
        return factType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return factType + "." + name;
    }

    @Override
    public int compareTo(final FieldBase field) {
        if (factType.equals(field.factType)
                && name.equals(field.name)) {
            return 0;
        } else if (factType.equals(field.factType)) {
            return name.compareTo(field.name);
        } else {
            return factType.compareTo(field.factType);
        }
    }

    @Override
    public int hashCode() {
        int result = ~~factType.hashCode();
        result = 31 * result + ~~name.hashCode();
        return ~~result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FieldBase field = (FieldBase) o;

        if (!factType.equals(field.factType)) {
            return false;
        }
        return name.equals(field.name);
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(NAME,
                        name)
        };
    }

    @Override
    public String toHumanReadableString() {
        return name;
    }
}
