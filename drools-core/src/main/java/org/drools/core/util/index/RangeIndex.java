package org.drools.core.util.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

public class RangeIndex<K extends Comparable, V> implements Serializable {

    private final TreeMap<IndexKey<K>, V> map = new TreeMap<>();

    public V addIndex(IndexType indexType, K key, V value) {
        return map.put(new IndexKey<>(indexType, key), value);
    }

    public void removeIndex(IndexType indexType, K key) {
        map.remove(new IndexKey<>(indexType, key));
    }

    public Collection<V> getValues(K key) {
        return map.subMap(new IndexKey<>(IndexType.LT, key), false, new IndexKey<>(IndexType.GT, key), false).values();
    }

    public Collection<V> getAllValues() {
        return map.values();
    }

    public enum IndexType {

        LT(0),
        LE(0),
        GE(1),
        GT(1);

        private int direction;

        IndexType(int direction) {
            this.direction = direction;
        }
    }

    private static class IndexKey<K extends Comparable> implements Comparable<IndexKey<K>>, Serializable {

        private final IndexType indexType;
        private final K key;

        public IndexKey(IndexType indexType, K key) {
            this.indexType = indexType;
            this.key = key;
        }

        @Override
        public int compareTo(IndexKey<K> o) {
            int directionDiff = indexType.direction - o.indexType.direction;
            if (directionDiff != 0) {
                return directionDiff;
            }
            int orderDiff = key.compareTo(o.key);
            return orderDiff != 0 ? orderDiff : indexType.compareTo(o.indexType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((indexType == null) ? 0 : indexType.hashCode());
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IndexKey other = (IndexKey) obj;
            if (indexType != other.indexType)
                return false;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }
    }
}
