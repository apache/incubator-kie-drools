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

package org.drools.core.util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicBitwiseLong extends AtomicLong {

    public AtomicBitwiseLong(long initialValue) {
        super(initialValue);
    }

    public AtomicBitwiseLong() {
        super();
    }

    public long getAndBitwiseOr(long mask) {
        while (true) {
            long current = get();
            if (compareAndSet(current, current | mask ) ) {
                return current;
            }
        }
    }

    public long getAndBitwiseAnd(long mask) {
        while (true) {
            long current = get();
            if (compareAndSet(current, current & mask ) ) {
                return current;
            }
        }
    }

    public long getAndBitwiseXor(long mask) {
        while (true) {
            long current = get();
            if (compareAndSet(current, current ^ mask ) ) {
                return current;
            }
        }
    }

    public long getAndBitwiseReset(long mask) {
        while (true) {
            long current = get();

            if (compareAndSet(current, current & (~mask) ) ) {
                return current;
            }
        }
    }
}
