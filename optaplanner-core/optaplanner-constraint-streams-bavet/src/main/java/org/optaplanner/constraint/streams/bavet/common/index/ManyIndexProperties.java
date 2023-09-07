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

import java.util.Arrays;

import org.optaplanner.core.impl.util.Pair;
import org.optaplanner.core.impl.util.Quadruple;
import org.optaplanner.core.impl.util.Triple;

final class ManyIndexProperties implements IndexProperties {

    private final Object[] properties;

    ManyIndexProperties(Object... properties) {
        this.properties = properties;
    }

    @Override
    public <Type_> Type_ toKey(int index) {
        return (Type_) properties[index];
    }

    @Override
    public <Type_> Type_ toKey(int from, int to) {
        switch (to - from) {
            case 1:
                return toKey(from);
            case 2:
                return (Type_) Pair.of(toKey(from), toKey(from + 1));
            case 3:
                return (Type_) Triple.of(toKey(from), toKey(from + 1),
                        toKey(from + 2));
            case 4:
                return (Type_) Quadruple.of(toKey(from), toKey(from + 1),
                        toKey(from + 2), toKey(from + 3));
            default:
                return (Type_) new IndexerKey(this, from, to);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ManyIndexProperties)) {
            return false;
        }
        ManyIndexProperties other = (ManyIndexProperties) o;
        return Arrays.equals(properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(properties);
    }

    @Override
    public String toString() {
        return Arrays.toString(properties);
    }

}
