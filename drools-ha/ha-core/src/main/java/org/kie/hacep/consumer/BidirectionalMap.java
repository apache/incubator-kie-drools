/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.hacep.consumer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class BidirectionalMap<K, V> extends HashMap<K, V> implements Serializable {

    private HashMap<V, K> inverseMap = new HashMap<>();

    @Override
    public V remove(Object key) {
        V val = super.remove(key);
        inverseMap.remove(val);
        return val;
    }

    @Override
    public V put(K key, V value) {
        inverseMap.put(value, key);
        return super.put(key, value);
    }

    public K getKey(V value) {
        return inverseMap.get(value);
    }

    public K removeValue(V value) {
        K key = inverseMap.get(value);
        super.remove( key );
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inverseMap = (HashMap<V, K>) inputStream.readObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(inverseMap);
    }
}
