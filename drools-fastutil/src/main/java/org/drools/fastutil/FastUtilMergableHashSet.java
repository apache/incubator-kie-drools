package org.drools.fastutil;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;

import static it.unimi.dsi.fastutil.HashCommon.arraySize;

public class FastUtilMergableHashSet<K> extends ObjectOpenCustomHashSet<K> {

    public FastUtilMergableHashSet(Strategy strategy) {
        super(strategy);
    }

    public FastUtilMergableHashSet(int expected, Strategy strategy) {
        super(expected, strategy);
    }

    public K compute(final K k, final java.util.function.BiFunction<? super K, ? super K, ? extends K> remappingFunction) {
        K curr;
        final K[] key = this.key;
        K newKey = null;
        int pos;
        // The starting point.

        if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == null) || strategy.equals(k,curr)) {
            newKey = remappingFunction.apply(k, curr);
            key[pos] = (K) newKey;
        }

        // There's always an unused entry.
        if (newKey == null) {
            while (true) {
                if ((((curr = key[pos = (pos + 1) & mask]) == null)) || strategy.equals(k, curr)) {
                    newKey = remappingFunction.apply(k, curr);
                    break;
                }
            }
        }

        if (curr == null && newKey != null) {
            key[pos] = (K) newKey;
            if (size++ >= maxFill) rehash(arraySize(size + 1, f));
        } else if (curr != null && newKey == null) {
            removeEntry(pos);
        }
        return newKey;
    }

    private boolean removeEntry(final int pos) {
        // For some reason this method was private, so needed to copy it.
        size--;
        shiftKeys(pos);
        if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
        return true;
    }

}
