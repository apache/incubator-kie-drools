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

public class EmptyBitMask extends SingleLongBitMask implements BitMask, EmptyMask {

    private static final EmptyBitMask INSTANCE = new EmptyBitMask();

    private EmptyBitMask() { }

    public static EmptyBitMask get() {
        return INSTANCE;
    }

    @Override
    public BitMask set(int index) {
        return BitMask.Factory.getEmpty(index+1).set(index);
    }

    @Override
    public BitMask setAll(BitMask mask) {
        return mask.isEmpty() ? this : mask.clone();
    }

    @Override
    public BitMask reset(int index) {
        return this;
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        return this;
    }

    @Override
    public boolean isSet(int index) {
        return false;
    }

    @Override
    public boolean isAllSet() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean intersects(BitMask mask) {
        return false;
    }

    @Override
    public EmptyBitMask clone() {
        return this;
    }

    @Override
    public String getInstancingStatement() {
        return EmptyBitMask.class.getCanonicalName() + ".get()";
    }

    @Override
    public long asLong() {
        return 0L;
    }
}
