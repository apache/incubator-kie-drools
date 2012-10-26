package org.drools.builder.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class EvictionCache<K, V> implements Map<K, V> {

    public static final long MINUTE = 60 * 1000;

    private final Map<K, V> cache = new ConcurrentHashMap<K, V>();
    private final SortedSet<TimedEntry> accessedKey = new TreeSet<TimedEntry>();

    private final Timer timer;
    private final long evictionTime;

    public EvictionCache(long evictionTime) {
        this(evictionTime, DEFAULT_TIMER);
    }

    public EvictionCache(long evictionTime, Timer timer) {
        this.evictionTime = evictionTime;
        this.timer = timer;
    }

    public int size() {
        cleanupExpired();
        return cache.size();
    }

    public boolean isEmpty() {
        cleanupExpired();
        return cache.isEmpty();
    }

    public boolean containsKey(Object key) {
        cleanupExpired();
        return cache.containsKey(key);
    }

    public boolean containsValue(Object value) {
        cleanupExpired();
        return cache.containsValue(value);
    }

    public V get(Object key) {
        cleanupExpired();
        V value = cache.get(key);
        if (value != null) {
            registerAccess(key);
        }
        return value;
    }

    public V put(K key, V value) {
        cleanupExpired();
        registerAccess(key);
        return cache.put(key, value);
    }

    public V remove(Object key) {
        cleanupExpired();
        accessedKey.remove(new TimedEntry(timer.getTime(), key));
        return cache.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        cleanupExpired();
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            registerAccess(entry.getKey());
            cache.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        accessedKey.clear();
        cache.clear();
    }

    public Set<K> keySet() {
        cleanupExpired();
        return cache.keySet();
    }

    public Collection<V> values() {
        cleanupExpired();
        return cache.values();
    }

    public Set<Entry<K, V>> entrySet() {
        cleanupExpired();
        return cache.entrySet();
    }

    private void registerAccess(Object key) {
        TimedEntry timedEntry = new TimedEntry(timer.getTime(), key);
        accessedKey.remove(timedEntry);
        accessedKey.add(timedEntry);
    }

    private void cleanupExpired() {
        while ( !accessedKey.isEmpty() ) {
            TimedEntry timedEntry = accessedKey.first();
            if ( isExpired(timedEntry) ) {
                accessedKey.remove(timedEntry);
                cache.remove(timedEntry.key);
            } else {
                break;
            }
        }
    }

    private boolean isExpired(TimedEntry entry) {
        return timer.getTime() - entry.time > evictionTime;
    }

    private static class TimedEntry implements Comparable<TimedEntry> {
        private final long time;
        private final Object key;

        private TimedEntry(long time, Object key) {
            this.time = time;
            this.key = key;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TimedEntry && key.equals( ((TimedEntry)obj).key );
        }

        public int compareTo(TimedEntry other) {
            if (equals(other)) {
                return 0;
            }
            if (time == other.time) {
                return key.hashCode() - other.key.hashCode();
            }
            return time > other.time ? 1 : -1;
        }
    }

    public interface Timer {
        long getTime();
    }

    private static final Timer DEFAULT_TIMER = new SystemTimer();
    private static class SystemTimer implements Timer {
        public long getTime() {
            return System.currentTimeMillis();
        }
    }
}
