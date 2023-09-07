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

final class SingleIndexProperties implements IndexProperties {

    private final Object property;

    SingleIndexProperties(Object property) {
        this.property = property;
    }

    @Override
    public <Type_> Type_ toKey(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Impossible state: index (" + index + ") != 0");
        }
        return (Type_) property;
    }

    @Override
    public <Type_> Type_ toKey(int from, int to) {
        if (to != 1) {
            throw new IllegalArgumentException("Impossible state: key from (" + from + ") to (" + to + ").");
        }
        return toKey(from);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingleIndexProperties)) {
            return false;
        }
        SingleIndexProperties other = (SingleIndexProperties) o;
        return Objects.equals(property, other.property);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        return Objects.hashCode(property);
    }

    @Override
    public String toString() {
        return "[" + property + "]";
    }

}
