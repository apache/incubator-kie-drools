/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.definition.type.FactField;
import org.drools.definition.type.FactType;

/**
 * DO NOT USE THIS YET. Use FactType instead.
 *
 * @author michaelneale
 *
 */
public class DynamicFact implements Map<String, Object> {

    private Object bean;
    private FactType typeDef;

    public DynamicFact(Object bean, FactType typeDef) {
        this.bean = bean;
        this.typeDef = typeDef;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return this.typeDef.getField((String) key) != null;
    }

    public boolean containsValue(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {

        Set set = new HashSet();

        List<FactField> list = this.typeDef.getFields();
        for (FactField factField : list) {
        	final FactField ff = factField;
        	Map.Entry<String, Object> ent = new Map.Entry<String, Object>() {

        		public String getKey() {
        			return ff.getName();
        		}

        		public Object getValue() {
        			return typeDef.get(bean, ff.getName());
        		}

        		public Object setValue(Object o) {
        			typeDef.set(bean, ff.getName(), o);
        			return o;
        		}

        	};
        }

        return set;

    }

    public Object get(Object fieldName) {
        return this.typeDef.get(bean, (String) fieldName);
    }

    public boolean isEmpty() {
        return this.typeDef.getFields().size() == 0;
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    public Object put(String key, Object value) {
        Object old = this.typeDef.get(bean, key);
        this.typeDef.set(this.bean, key, value);
        return old;
    }

    public void putAll(Map arg0) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object arg0) {
        throw new UnsupportedOperationException();	}

    public int size() {
        return this.typeDef.getFields().size();
    }

    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the underlying fact itself. This is what you asset into the session/engine.
     */
    public Object getFactObject() {
        return this.bean;
    }



}
