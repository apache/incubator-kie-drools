package org.drools.core.ruleunit.impl;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.DataProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListDataStreamTest {

    @Test
    public void testCreate() {
        Counter counter = new Counter();
        ListDataStream<Integer> integers = ListDataStream.create(1, 2, 3);
        integers.subscribe(counter);
        assertEquals(3, counter.count);
    }

    @Test
    public void testAppend() {
        Counter counter = new Counter();
        ListDataStream<Integer> integers = ListDataStream.create();
        integers.subscribe(counter);
        assertEquals(0, counter.count);
        integers.append(10);
        assertEquals(1, counter.count);
        integers.append(20);
        integers.append(30);
        assertEquals(3, counter.count);
    }

    private static class Counter<T> implements DataProcessor<T> {

        int count = 0;

        @Override
        public FactHandle insert(DataHandle handle, T object) {
            count++;
            return null;
        }

        @Override
        public void update(DataHandle handle, T object) {

        }

        @Override
        public void delete(DataHandle handle) {

        }
    }

    ;
}