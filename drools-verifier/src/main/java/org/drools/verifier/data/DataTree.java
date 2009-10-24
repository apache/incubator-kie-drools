package org.drools.verifier.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataTree<K, V> {
	private Map<K, Set<V>> map = new TreeMap<K, Set<V>>();

	public void put(K key, V value) {
		if (map.containsKey(key)) {
			Set<V> set = map.get(key);
			set.add(value);
		} else {
			Set<V> set = new TreeSet<V>();
			set.add(value);
			map.put(key, set);
		}
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Set<V> getBranch(K key) {
		Set<V> set = map.get(key);
		if (set != null) {
			return set;
		} else {
			return Collections.emptySet();
		}
	}

	public Collection<V> values() {
		Collection<V> values = new ArrayList<V>();

		for (Set<V> set : map.values()) {
			for (V value : set) {
				values.add(value);
			}
		}

		return values;
	}

	public boolean remove(K key, V value) {
		Set<V> set = map.get(key);

		if (set != null) {
			return set.remove(value);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return values().toString();
	}
}
