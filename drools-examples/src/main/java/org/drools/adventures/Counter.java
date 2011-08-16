package org.drools.adventures;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
    private AtomicLong value = new AtomicLong(0);
    
    public long getAndIncrement() {
        return value.getAndIncrement();
    }
}
