/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.drools.core.util.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

import org.drools.core.util.FastIterator;
import org.drools.core.util.RBTree;

public class RangeIndex<K extends Comparable, V> implements Serializable {

//    private final TreeMap<IndexKey<K>, V> map = new TreeMap<>();
    private final RBTree<IndexKey<K>, V> tree = new RBTree<>();

    public void addIndex(IndexType indexType, K key, V value) {
        tree.insert(new IndexKey<>(indexType, key), value);
    }

    public void removeIndex(IndexType indexType, K key) {
        tree.delete(new IndexKey<>(indexType, key));
    }

    public FastIterator getValuesIterator(K key) {
        return tree.range(new IndexKey<>(IndexType.LT, key), false, new IndexKey<>(IndexType.GT, key), false);
    }

    public FastIterator getAllValuesIterator() {
        return tree.fastIterator();
    }

    public enum IndexType {
        LT(0), LE(0), GE(1), GT(1);

        private int direction;

        IndexType(int direction) {
            this.direction = direction;
        }
    }

    private static class IndexKey<K extends Comparable> implements Comparable<IndexKey<K>> {

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
    }
}