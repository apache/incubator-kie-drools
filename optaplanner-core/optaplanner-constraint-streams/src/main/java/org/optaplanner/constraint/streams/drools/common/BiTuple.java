/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class BiTuple<A, B> implements FactTuple {
    public final A a;
    public final B b;
    private final int hashCode;

    public BiTuple(A a, B b) {
        this.a = a;
        this.b = b;
        this.hashCode = Objects.hash(a, b);
    }

    @Override
    public List<Object> asList() {
        return Arrays.asList(a, b);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final BiTuple<?, ?> other = (BiTuple<?, ?>) o;
        return hashCode == other.hashCode &&
                Objects.equals(a, other.a) &&
                Objects.equals(b, other.b);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "BiTuple(" + a + ", " + b + ")";
    }
}
