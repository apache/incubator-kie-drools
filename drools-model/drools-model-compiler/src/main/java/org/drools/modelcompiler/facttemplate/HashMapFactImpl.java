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

import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.model.PrototypeFact;

public class HashMapFactImpl implements Fact, PrototypeFact {

    private FactTemplate factTemplate;

    private Map<String, Object> valuesMap = new HashMap<>();

    public HashMapFactImpl( FactTemplate factTemplate ) {
        this.factTemplate = factTemplate;
    }

    @Override
    public FactTemplate getFactTemplate() {
        return factTemplate;
    }

    @Override
    public boolean has( String name ) {
        return valuesMap.containsKey( name );
    }

    @Override
    public Object get( String name ) {
        return valuesMap.get(name);
    }

    @Override
    public void set( String name, Object value ) {
        valuesMap.put(name, value);
    }

    @Override
    public Map<String, Object> asMap() {
        return valuesMap;
    }

    @Override
    public String toString() {
        return factTemplate + "; values = " + valuesMap;
    }
}
