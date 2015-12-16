/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

public class EnvironmentImpl implements Environment {

    // The default concurrencyLevel is 16: if users have enough threads
    // that (16^(1.5))=64 *concurrent* updates are possible/likely
    // then the concurrencyLevel will need to be upgraded,
    // but that situation is fairly unlikely
    private Map<String, Object> environment = new NullValueConcurrentHashMap<String, Object>();

    private Environment delegate;
    
    public void setDelegate(Environment delegate) {
        this.delegate = delegate;
    }

    public Object get(String identifier) {
        Object object = environment.get(identifier);
        if ( object == null && delegate != null ) {
            object = this.delegate.get( identifier );
        }
        return object;
    }

    public void set(String name, Object object) {
        environment.put(name, object);
    }

    /**
     * This class adds the possibility of storing null values in the {@link ConcurrentHashMap}, 
     * since storing null values in an {@link Environment} is something that happens in the kie code. 
     * 
     * This class is needed for the {@link Environment} implementation since happens-before is
     * not guaranteed with a normal {@link HashMap}. Not having guaranteed happens-before can lead to 
     * race-conditions, especially when using a {@link KieSession} as a Singleton in a multi-threaded 
     * environment. 
     *
     * @param <K> The key type
     * @param <V> The value type
     */
    private static class NullValueConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private static Object NULL = new Object();

        public V put(K key, V value) {
            if (value != null) {
                value = super.put(key, value);
            } else {
                value = super.put(key, (V) NULL);
                if (value == NULL) {
                    return null;
                }
            }
            return value;
        }

        public V get(Object key) {
            Object value = super.get(key);
            if (value == NULL) {
                return null;
            }
            return (V) value;
        }
        
        public boolean containsValue(Object value) { 
           if( value == null ) { 
               return super.containsValue(NULL);
           }
           return super.containsValue(value);
        }
        
        public void putAll(Map<? extends K, ? extends V> m) { 
            throw new UnsupportedOperationException("The putAll(Map<K, V> m) method is not supported on this implementation.");
        }
        
        public Collection<V> values() { 
            throw new UnsupportedOperationException("The values() method is not supported on this implementation.");
        }
        
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException("The entrySet() method is not supported on this implementation.");
        }
    }
}
