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
package org.drools.verifier.core.checks;

import java.util.Comparator;
import java.util.Objects;

public class ComparableWrapper
        implements Comparable<ComparableWrapper> {

    public final static ComparableWrapper MIN_VALUE = new ComparableWrapper(null, Type.NEGATIVE_INFINITE);
    public final static ComparableWrapper MAX_VALUE = new ComparableWrapper(null, Type.INFINITE);

    enum Type {
        NEGATIVE_INFINITE,
        NORMAL,
        INFINITE
    }

    private final Comparable value;
    private final Type type;

    public ComparableWrapper(final Comparable value) {
        this(value, Type.NORMAL);
    }

    private ComparableWrapper(final Comparable value,
                              final Type type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public int compareTo(final ComparableWrapper other) {

        if (!Objects.equals(type, Type.NORMAL) || !Objects.equals(other.type, Type.NORMAL)) {
            return type.compareTo(other.type);
        } else {
            Comparator<Comparable> nullFirstCompare = Comparator.nullsFirst(Comparable::compareTo);
            return nullFirstCompare.compare(value, other.value);
        }
    }

    public Comparable getValue() {
        return value;
    }
}