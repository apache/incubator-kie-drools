/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.facttemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.model.PrototypeFact;

public class HashMapFactImpl implements Fact, PrototypeFact {

    private static AtomicLong staticFactId = new AtomicLong();

    private FactTemplate factTemplate;
    private long factId;

    private Map<String, Object> valuesMap = new HashMap<>();

    public HashMapFactImpl( FactTemplate factTemplate ) {
        factId = staticFactId.addAndGet(1);
        this.factTemplate = factTemplate;
    }

    @Override
    public long getFactId() {
        return factId;
    }

    @Override
    public FactTemplate getFactTemplate() {
        return factTemplate;
    }

    @Override
    public Object getFieldValue(int index) {
        FieldTemplate field = factTemplate.getFieldTemplate(index);
        return valuesMap.get(field.getName());
    }

    @Override
    public Object getFieldValue(String key) {
        return valuesMap.get(key);
    }

    @Override
    public void setFieldValue(int index, Object value) {
        FieldTemplate field = factTemplate.getFieldTemplate(index);
        valuesMap.put( field.getName(), value );
    }

    @Override
    public void setFieldValue(String key, Object value) {
        valuesMap.put(key, value);
    }

    @Override
    public Object get( int index ) {
        return getFieldValue( index );
    }

    @Override
    public Object get( String name ) {
        return getFieldValue( name );
    }

    @Override
    public void set( String name, Object value ) {
        setFieldValue( name, value );
    }

    @Override
    public void set( int index, Object value ) {
        setFieldValue( index, value );
    }
}
