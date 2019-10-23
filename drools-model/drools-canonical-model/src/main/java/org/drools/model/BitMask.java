/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model;

import java.io.Serializable;
import java.util.Arrays;

import org.drools.model.bitmask.BitMaskUtil;
import org.drools.model.bitmask.LongBitMask;
import org.drools.model.bitmask.OpenBitSet;

public interface BitMask extends Serializable, Cloneable {

    BitMask set( int index );
    BitMask setAll( BitMask mask );

    BitMask reset( int index );
    BitMask resetAll( BitMask mask );

    boolean isSet( int index );
    boolean isAllSet();

    boolean isEmpty();

    boolean intersects( BitMask mask );

    BitMask clone();

    String getInstancingStatement();

    Class<?> getPatternClass();
    void setPatternClass( Class<?> patternClass );

    static BitMask getPatternMask( Class<?> clazz, String... listenedProperties ) {
        BitMask bitMask = BitMaskUtil.calculatePatternMask( clazz, Arrays.asList( listenedProperties ) );
        bitMask.setPatternClass( clazz );
        return bitMask;
    }

    static BitMask getPatternMask( DomainClassMetadata metadata, String... listenedProperties ) {
        BitMask bitMask = BitMaskUtil.calculatePatternMask( metadata, true, listenedProperties );
        bitMask.setPatternClass( metadata.getDomainClass() );
        return bitMask;
    }

    static BitMask getEmpty(int numBits) {
        return numBits <= 64 ? new LongBitMask() : new OpenBitSet( numBits);
    }

    static BitMask getFull(int numBits) {
        if (numBits <= 64) {
            return new LongBitMask(-1L);
        }
        int nWords = (numBits / 64) + 1;
        long[] bits = new long[nWords];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = -1L;
        }
        return new OpenBitSet(bits, nWords);
    }
}
