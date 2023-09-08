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
package org.drools.verifier.core.index.keys;

import java.util.HashSet;

import static java.util.stream.Collectors.joining;

public class Values<T extends Comparable>
        extends HashSet<T> {

    public Values(final Comparable... list) {
        super();

        for (final Comparable comparable : list) {
            add((T) comparable);
        }
    }

    public Values() {
    }

    @Override
    public String toString() {
        return stream().map(Object::toString).collect(joining(", "));
    }

    public boolean isThereChanges(final Values otherValues) {
        if (this.isEmpty() && !otherValues.isEmpty()) {
            return true;
        } else if (!this.isEmpty() && otherValues.isEmpty()) {
            return true;
        } else if (this.isEmpty() && otherValues.isEmpty()) {
            return false;
        } else if (this.size() != otherValues.size()) {
            return true;
        } else if (!areValuesEqual(otherValues)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean areValuesEqual(final Values otherValues) {

        if (!otherValues.containsAll(this)) {
            return false;
        }

        if (!containsAll(otherValues)) {
            return false;
        }

        return true;
    }

    public boolean containsAny(final Values values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }

        return false;
    }

    public static <T extends Comparable> Values<T> nullValue() {
        final Values<T> comparables = new Values<>();
        comparables.add(null);
        return comparables;
    }
}
