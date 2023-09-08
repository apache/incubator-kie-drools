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

public class AllSetButLastBitMask extends SingleLongBitMask implements BitMask, AllSetMask {

    private static final AllSetButLastBitMask INSTANCE = new AllSetButLastBitMask();

    private AllSetButLastBitMask() { }

    public static AllSetButLastBitMask get() {
        return INSTANCE;
    }

    @Override
    public BitMask set(int index) {
        return index == 0 ? AllSetBitMask.get() : this;
    }

    @Override
    public BitMask setAll(BitMask mask) {
        return mask.isSet(0) ? AllSetBitMask.get() : this;
    }

    @Override
    public BitMask reset(int index) {
        if (index == 0) {
            return this;
        }
        return BitMask.getFull(index+1).reset(0).reset(index);
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        if (mask instanceof EmptyMask) {
            return this;
        }
        if (mask instanceof AllSetMask) {
            return EmptyBitMask.get();
        }
        return BitMask.getFull(mask instanceof LongBitMask ? 1 : 65).reset(0).resetAll(mask);
    }

    @Override
    public boolean isSet(int index) {
        return index != 0;
    }

    @Override
    public boolean isAllSet() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean intersects(BitMask mask) {
        if (mask instanceof AllSetMask) {
            return true;
        }
        if (mask instanceof EmptyMask) {
            return true;
        }
        return mask instanceof LongBitMask ?
               (Long.MAX_VALUE & ((LongBitMask)mask).asLong()) != 0 :
               ((OpenBitSet)mask).nextSetBit(1) != -1;
    }

    @Override
    public long asLong() {
        return Long.MAX_VALUE;
    }

    @Override
    public AllSetButLastBitMask clone() {
        return this;
    }

    @Override
    public String getInstancingStatement() {
        return AllSetButLastBitMask.class.getCanonicalName() + ".get()";
    }
}
