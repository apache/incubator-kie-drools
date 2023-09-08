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
import org.drools.verifier.core.index.keys.IndexKey;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasIndex;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public class Column
        implements HasKeys,
                   HasIndex {

    private final UUIDKey uuidKey;

    private UpdatableKey<Column> indexKey;

    public Column(final int columnIndex,
                  final AnalyzerConfiguration configuration) {
        this.indexKey = new UpdatableKey<>(IndexKey.INDEX_ID,
                                           columnIndex);
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers index() {
        return new Matchers(IndexKey.INDEX_ID);
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                IndexKey.INDEX_ID
        };
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                indexKey
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public int getIndex() {
        return (int) indexKey.getSingleValueComparator();
    }

    @Override
    public void setIndex(final int index) {
        if (indexKey.getSingleValue()
                .equals(new Value(index))) {
            return;
        } else {

            final UpdatableKey<Column> oldKey = indexKey;
            final UpdatableKey<Column> newKey = new UpdatableKey<>(IndexKey.INDEX_ID,
                                                                   index);
            indexKey = newKey;

            oldKey.update(newKey,
                          this);
        }
    }
}
