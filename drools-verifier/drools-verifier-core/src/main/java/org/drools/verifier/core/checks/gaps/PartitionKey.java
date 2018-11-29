/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.checks.gaps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.drools.verifier.api.reporting.gaps.PartitionCondition;

public class PartitionKey {

    public static PartitionKey EMPTY_KEY = new PartitionKey(new Object[0], Collections.EMPTY_LIST);

    private final Object[] keys;
    private List<PartitionCondition> conditions;

    public PartitionKey(final Object[] keys,
                        final List<PartitionCondition> conditions) {
        this.keys = keys;
        this.conditions = conditions;
    }

    @Override
    public boolean equals(final Object obj) {
        return Arrays.equals(keys, ((PartitionKey) obj).keys);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keys);
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }

    boolean hasNulls() {
        return Stream.of(keys).anyMatch(Objects::isNull);
    }

    public boolean subsumes(final PartitionKey other) {
        return IntStream.range(0, keys.length).allMatch(i -> keys[i] == null || Objects.equals(keys[i], other.keys[i]));
    }

    public List<PartitionCondition> getConditions() {
        return conditions;
    }
}
