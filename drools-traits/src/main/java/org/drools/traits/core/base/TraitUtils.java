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
package org.drools.traits.core.base;

import java.util.BitSet;

public interface TraitUtils {

    static boolean supersetOrEqualset(BitSet n1, BitSet n2 ) {
        BitSet x;
        int l1 = n1.length();
        int l2 = n2.length();

        if ( l1 > l2 ) {
            x = new BitSet( l2 );
            x.or( n2 );
            x.and( n1 );
        } else {
            x = new BitSet( l1 );
            x.or( n1 );
            x.and( n2 );
        }
        return x.equals( n2 );
    }

}
