/*
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

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Objects;

final class TwoIndexProperties implements IndexProperties {

    private final Object propertyA;
    private final Object propertyB;

    TwoIndexProperties(Object propertyA, Object propertyB) {
        this.propertyA = propertyA;
        this.propertyB = propertyB;
    }

    @Override
    public <Type_> Type_ toKey(int index) {
        switch (index) {
            case 0:
                return (Type_) propertyA;
            case 1:
                return (Type_) propertyB;
            default:
                throw new IllegalArgumentException("Impossible state: index (" + index + ") != 0");
        }
    }

    @Override
    public <Type_> Type_ toKey(int from, int to) {
        switch (to - from) {
            case 1:
                return toKey(from);
            case 2:
                if (from != 0 || to != 2) {
                    throw new IllegalArgumentException("Impossible state: key from (" + from + ") to (" + to + ").");
                }
                return (Type_) this;
            default:
                throw new IllegalArgumentException("Impossible state: key from (" + from + ") to (" + to + ").");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TwoIndexProperties)) {
            return false;
        }
        TwoIndexProperties other = (TwoIndexProperties) o;
        return Objects.equals(propertyA, other.propertyA)
                && Objects.equals(propertyB, other.propertyB);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        int result = Objects.hashCode(propertyA);
        result = 31 * result + Objects.hashCode(propertyB);
        return result;
    }

    @Override
    public String toString() {
        return "[" + propertyA + ", " + propertyB + "]";
    }

}
