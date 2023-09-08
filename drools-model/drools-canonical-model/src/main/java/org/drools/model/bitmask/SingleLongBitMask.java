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
package org.drools.model.bitmask;

import org.drools.model.BitMask;

public abstract class SingleLongBitMask implements BitMask {

    private Class<?> patternClass;

    public abstract long asLong();

    public abstract SingleLongBitMask clone();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof SingleLongBitMask && asLong() == ((SingleLongBitMask) o).asLong();
    }

    @Override
    public final int hashCode() {
        return (int) (asLong() ^ (asLong() >>> 32));
    }

    @Override
    public final String toString() {
        return "" + asLong();
    }

    @Override
    public Class<?> getPatternClass() {
        return patternClass;
    }

    @Override
    public void setPatternClass( Class<?> patternClass ) {
        this.patternClass = patternClass;
    }
}
