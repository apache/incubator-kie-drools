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
