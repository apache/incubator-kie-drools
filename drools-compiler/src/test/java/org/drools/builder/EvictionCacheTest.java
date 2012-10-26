package org.drools.builder;

import org.drools.builder.impl.EvictionCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EvictionCacheTest {

    @Test
    public void testEvictionCache() {
        FakeTimer timer = new FakeTimer();
        EvictionCache<String, Integer> cache = new EvictionCache<String, Integer>(10, timer);
        cache.put("One", 1);
        timer.after(3);
        cache.put("Two", 2);
        timer.after(3);
        cache.put("Three", 3);
        timer.after(3);

        assertEquals(new Integer(2), cache.get("Two"));
        timer.after(3);
        assertNull(cache.get("One"));

        timer.after(5);
        assertEquals(new Integer(2), cache.get("Two"));
        assertNull(cache.get("Three"));

        timer.after(15);
        assertTrue(cache.isEmpty());
    }

    public static class FakeTimer implements EvictionCache.Timer {
        private long time = 0;

        public long getTime() {
            return time;
        }

        public void after(long delay) {
            time += delay;
        }
    }
}
