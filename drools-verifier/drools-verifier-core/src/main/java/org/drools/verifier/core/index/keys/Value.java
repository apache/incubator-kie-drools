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

public class Value
        implements Comparable<Value> {

    private final Comparable comparable;

    public Value(final Comparable comparable) {
        this.comparable = comparable;
    }

    public Comparable getComparable() {
        return comparable;
    }

    @Override
    public String toString() {
        return "" + comparable;
    }

    @Override
    public int compareTo(final Value value) {
        if (comparable == null && value.comparable == null) {
            return 0;
        } else if (comparable == null) {
            return -1;
        } else if (value.comparable == null) {
            return 1;
        } else {
            try {
                return this.comparable.compareTo(value.comparable);
            } catch (final Exception cce) {
                return this.comparable.toString().compareTo(value.comparable.toString());
            }
        }
    }
}
