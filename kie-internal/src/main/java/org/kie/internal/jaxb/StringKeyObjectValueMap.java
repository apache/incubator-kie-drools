/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import static org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter.createJaxbStringObjectMapEntry;
import static org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter.deserializeObject;

/**
 * This implements {@link Map} in order to fool JSON..
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StringKeyObjectValueMap implements Map<String, Object> {

    @XmlElement(name="entry")
    public List<StringKeyObjectValueEntry> entries = new ArrayList<>();

    public void addEntry(StringKeyObjectValueEntry newEntry) {
       this.entries.add(newEntry);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if( key == null ) {
            return false;
        }
        for( StringKeyObjectValueEntry entry : entries ) {
            if( key.equals(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if( value == null ) {
            return false;
        }
        for( StringKeyObjectValueEntry entry : entries ) {
            Object entryVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
            if( value.equals(entryVal) ) {
               return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        if( key == null ) {
            return null;
        }
        for( StringKeyObjectValueEntry entry : entries ) {
            if( key != null && key.equals(entry.getKey())) {
                return deserializeObject(entry.getBytes(), entry.getClassName(), key.toString());
            }
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        Object oldVal = get(key);
        StringKeyObjectValueEntry newEntry = createJaxbStringObjectMapEntry(value, key);
        entries.add(newEntry);
        return oldVal;
    }

    @Override
    public Object remove(Object key) {
        Iterator<StringKeyObjectValueEntry> iter = entries.iterator();
        while( iter.hasNext() ) {
            StringKeyObjectValueEntry entry = iter.next();
            String entryKey = entry.getKey();
            if( key.equals(entryKey) ) {
                iter.remove();
                return deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for( Entry<?, ?> entry : m.entrySet() ) {
           StringKeyObjectValueEntry newEntry = createJaxbStringObjectMapEntry(entry.getValue(), entry.getKey().toString());
           entries.add(newEntry);
        }
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        for( StringKeyObjectValueEntry entry : entries ) {
            keySet.add(entry.getKey());
        }
        return keySet;
    }

    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<>();
        for( StringKeyObjectValueEntry entry : entries ) {
           Object newVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
           values.add(newVal);
        }
        return values;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        Set<java.util.Map.Entry<String, Object>> entrySet = new HashSet<>();
        for( StringKeyObjectValueEntry entry : entries ) {
           Object newVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
           String key = entry.getKey();
           Entry<String, Object> newEntry = new EntryImpl(key, newVal);
           entrySet.add(newEntry);
        }
        return entrySet;
    }

    private class EntryImpl implements Entry<String, Object> {

        private final String key;
        private Object val;

        public EntryImpl( String key, Object val) {
            this.key = key;
            this.val = val;
        }
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return val;
        }

        @Override
        public Object setValue(Object value) {
            Object oldVal = this.val;
            this.val = value;
            return oldVal;
        }

    }
}
