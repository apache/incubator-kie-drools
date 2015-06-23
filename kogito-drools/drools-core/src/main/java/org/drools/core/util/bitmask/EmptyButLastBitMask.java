/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util.bitmask;

public class EmptyButLastBitMask extends SingleLongBitMask implements BitMask, EmptyMask {

    private static final EmptyButLastBitMask INSTANCE = new EmptyButLastBitMask();

    private EmptyButLastBitMask() { }

    public static EmptyButLastBitMask get() {
        return INSTANCE;
    }

    @Override
    public BitMask set(int index) {
        return BitMask.Factory.getEmpty(index+1).set(index).set(0);
    }

    @Override
    public BitMask setAll(BitMask mask) {
        return mask.isEmpty() ? this : mask.clone().set(0);
    }

    @Override
    public BitMask reset(int index) {
        if (index == 0) {
            return EmptyBitMask.get();
        }
        return this;
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        if (mask.isSet(0)) {
            return EmptyBitMask.get();
        }
        return this;
    }

    @Override
    public boolean isSet(int index) {
        return index == 0;
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
        return mask.isSet(0);
    }

    @Override
    public EmptyButLastBitMask clone() {
        return this;
    }

    @Override
    public String getInstancingStatement() {
        return EmptyButLastBitMask.class.getCanonicalName() + ".get()";
    }

    @Override
    public long asLong() {
        return 1L;
    }
}
