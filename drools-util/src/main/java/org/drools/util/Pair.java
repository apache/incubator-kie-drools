package org.drools.util;

public class Pair<K, V> {

    private final K left;
    private final V right;

    public Pair(K k, V v) {
        this.left = k;
        this.right = v;
    }

    public K getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }
}
