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

package org.drools.util.bitmask;

import java.io.Serializable;
import java.util.Arrays;

public interface BitMask extends Serializable, Cloneable {

    BitMask set(int index);
    BitMask setAll(BitMask mask);

    BitMask reset(int index);
    BitMask resetAll(BitMask mask);

    boolean isSet(int index);
    boolean isAllSet();

    boolean isEmpty();

    boolean intersects(BitMask mask);

    BitMask clone();

    String getInstancingStatement();

    static BitMask getEmpty(int numBits) {
        return numBits <= 64 ? new LongBitMask() : new OpenBitSet(numBits);
    }

    static BitMask getFull(int numBits) {
        if (numBits <= 64) {
            return new LongBitMask(-1L);
        }
        int nWords = (numBits / 64) + 1;
        long[] bits = new long[nWords];
        Arrays.fill(bits, -1L);
        return new OpenBitSet(bits, nWords);
    }

}
