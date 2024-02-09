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

/**
 * This implements {@link Map} in order to fool JSON..
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StringKeyStringValueMap implements Map<String, String> {

    @XmlElement(name="entry")
    public List<StringKeyStringValueEntry> entries = new ArrayList<>();

    public void addEntry(StringKeyStringValueEntry newEntry) {
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
        for( StringKeyStringValueEntry entry : entries ) {
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
        for( StringKeyStringValueEntry entry : entries ) {
            Object entryVal = entry.getValue();
            if( value.equals(entryVal) ) {
               return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        if( key == null ) {
            return null;
        }
        for( StringKeyStringValueEntry entry : entries ) {
            if( key != null && key.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String put(String key, String value) {
        String oldVal = get(key);
        StringKeyStringValueEntry newEntry = new StringKeyStringValueEntry(key, value);
        entries.add(newEntry);
        return oldVal;
    }

    @Override
    public String remove(Object key) {
        Iterator<StringKeyStringValueEntry> iter = entries.iterator();
        while( iter.hasNext() ) {
            StringKeyStringValueEntry entry = iter.next();
            String entryKey = entry.getKey();
            if( key.equals(entryKey) ) {
                iter.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for( Entry<?, ?> entry : m.entrySet() ) {
            StringKeyStringValueEntry newEntry = new StringKeyStringValueEntry((String)entry.getKey(), (String)entry.getValue());
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
        for( StringKeyStringValueEntry entry : entries ) {
            keySet.add(entry.getKey());
        }
        return keySet;
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        for( StringKeyStringValueEntry entry : entries ) {
           values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        Set<java.util.Map.Entry<String, String>> entrySet = new HashSet<>();
        for( StringKeyStringValueEntry entry : entries ) {
           String newVal = entry.getValue();
           String key = entry.getKey();
           Entry<String, String> newEntry = new EntryImpl(key, newVal);
           entrySet.add(newEntry);
        }
        return entrySet;
    }

    private class EntryImpl implements Entry<String, String> {

        private final String key;
        private String val;

        public EntryImpl( String key, String val) {
            this.key = key;
            this.val = val;
        }
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return val;
        }

        @Override
        public String setValue(String value) {
            String oldVal = this.val;
            this.val = value;
            return oldVal;
        }

    }
}
