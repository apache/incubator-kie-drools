/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.Arrays;
import java.util.Objects;

/**
 * Used inside Drools-based constraint stream implementation to implement grouping operations. This is the carrier data
 * type which the grouping rule logically inserts into the Drools working memory, so that the following rules can work
 * with the results of the grouping.
 *
 * Instances of this class are equal if they have the same {@link #getRuleId()} and their {@link #getItem(int)} all
 * {@link Objects#equals(Object, Object)} as well. Drools will not re-insert a fact if equalling fact has already been
 * inserted before.
 */
public final class DroolsLogicalTuple {

    // Should match the maximum cardinality of streams we support. Currently it's 3 for TriStreams.
    private static final int MAXIMUM_SUPPORTED_CARDINALITY = 3;

    private final Object ruleId;
    private final Object[] items;

    public DroolsLogicalTuple(final Object ruleId, final Object... items) {
        Objects.requireNonNull(ruleId, "Logical tuple rule context must not be null.");
        this.ruleId = ruleId;
        int itemCount = items.length;
        if (itemCount == 0) { // such a tuple makes no sense
            throw new IllegalArgumentException("Logical tuple must have at least one element.");
        } else if (itemCount > MAXIMUM_SUPPORTED_CARDINALITY) {
            throw new IllegalArgumentException("Logical tuple must have at most 3 elements, has " + itemCount + ".");
        }
        this.items = new Object[itemCount]; // construct a new array to make the tuple truly immutable
        for (int i = 0; i < itemCount; i++) {
            Object item = items[i];
            Objects.requireNonNull(item, "Logical tuple does not accept null arguments.");
            this.items[i] = item;
        }
    }

    /**
     *
     * @return number of facts in this tuple. Always greater than zero.
     */
    public int getCardinality() {
        return items.length;
    }

    /**
     *
     * @return never null. Unique identifier of the rule that caused the tuple to be logically inserted.
     */
    public Object getRuleId() {
        return ruleId;
    }

    /**
     * Return a fact on n-th position in the tuple.
     * @param index required position in the tuple. Must be between 0 (incl.) and {@link #getCardinality()} (excl.)
     * @param <T> type that we're expecting to see. Method may throw an exception if the cast can not be made.
     * @throws ArrayIndexOutOfBoundsException when index not between 0 (incl.) and {@link #getCardinality()} (excl.)
     * @return never null.
     */
    public <T> T getItem(final int index) {
        return (T) items[index];
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final DroolsLogicalTuple that = (DroolsLogicalTuple) o;
        return Objects.equals(ruleId, that.ruleId) &&
                Arrays.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ruleId);
        result = 31 * result + Arrays.hashCode(items);
        return result;
    }

    @Override
    public String toString() {
        return "DroolsLogicalTuple{" +
                "ruleId='" + ruleId + '\'' +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
