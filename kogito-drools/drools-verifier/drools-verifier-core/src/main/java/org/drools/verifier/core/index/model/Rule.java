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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.IndexKey;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ComparableMatchers;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasIndex;
import org.drools.verifier.core.maps.util.HasKeys;

public class Rule
        implements Comparable<Rule>,
                   HasKeys,
                   HasIndex {

    private final Patterns patterns = new Patterns();
    private final Actions actions = new Actions();
    private final Conditions conditions = new Conditions();

    private final UUIDKey uuidKey;

    private UpdatableKey<Rule> indexKey;

    public Rule(final Integer rowNumber,
                final AnalyzerConfiguration configuration) {
        this.indexKey = new UpdatableKey<>(IndexKey.INDEX_ID,
                                           rowNumber);
        this.uuidKey = configuration.getUUID(this);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public static ComparableMatchers index() {
        return new ComparableMatchers(IndexKey.INDEX_ID);
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                IndexKey.INDEX_ID
        };
    }

    public Integer getRowNumber() {
        return getIndex();
    }

    public Patterns getPatterns() {
        return patterns;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Actions getActions() {
        return actions;
    }

    @Override
    public int compareTo(final Rule rule) {
        return 0;
    }

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

            final UpdatableKey<Rule> oldKey = indexKey;

            final UpdatableKey<Rule> newKey = new UpdatableKey<>(IndexKey.INDEX_ID,
                                                                 index);
            indexKey = newKey;

            oldKey.update(newKey,
                          this);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Rule rule = (Rule) o;

        if (!uuidKey.equals(rule.uuidKey)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return uuidKey.hashCode();
    }
}