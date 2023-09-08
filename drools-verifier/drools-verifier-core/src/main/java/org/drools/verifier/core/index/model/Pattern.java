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
import org.drools.verifier.core.util.PortablePreconditions;

public class Pattern
        implements HasKeys {

    private static final KeyDefinition NAME = KeyDefinition.newKeyDefinition()
            .withId("name")
            .build();
    private static final KeyDefinition BOUND_NAME = KeyDefinition.newKeyDefinition()
            .withId("boundName")
            .build();

    private final UUIDKey uuidKey;
    private final String name;
    private final Fields fields = new Fields();
    private final String boundName;
    private final ObjectType objectType;

    public Pattern(final String boundName,
                   final ObjectType objectType,
                   final AnalyzerConfiguration configuration) {
        this.boundName = PortablePreconditions.checkNotNull("boundName",
                                                            boundName);
        this.objectType = PortablePreconditions.checkNotNull("objectType",
                                                             objectType);
        this.name = objectType.getType();
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers boundName() {
        return new Matchers(BOUND_NAME);
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
                NAME,
                BOUND_NAME
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getName() {
        return name;
    }

    public String getBoundName() {
        return boundName;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Fields getFields() {
        return fields;
    }

    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(NAME,
                        name),
                new Key(BOUND_NAME,
                        boundName)
        };
    }
}
