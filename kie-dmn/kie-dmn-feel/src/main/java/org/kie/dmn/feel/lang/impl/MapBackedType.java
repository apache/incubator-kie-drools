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
package org.kie.dmn.feel.lang.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.Type;

/**
 * A map-based type descriptor
 */
public class MapBackedType
        implements CompositeType {

    public static final String TYPE_NAME = "__TYPE_NAME__";

    private String            name   = "[anonymous]";
    private Map<String, Type> fields = new LinkedHashMap<>();

    public MapBackedType() {
    }

    public MapBackedType(String typeName) {
        this.name = typeName;
    }

    public MapBackedType(String typeName, Map<String, Type> fields) {
        this.name = typeName;
        this.fields.putAll( fields );
    }

    @Override
    public String getName() {
        return this.name;
    }

    public MapBackedType addField(String name, Type type) {
        fields.put( name, type );
        return this;
    }

    @Override
    public Map<String, Type> getFields() {
        return fields;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        if (!(o instanceof Map)) {
            return false;
        }
        Map<?, ?> instance = (Map<?, ?>) o;
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( !instance.containsKey(f.getKey()) ) {
                return false;
            }
            Object instanceValueForKey = instance.get(f.getKey());
            if ( instanceValueForKey != null && !f.getValue().isInstanceOf(instanceValueForKey) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true;
        }
        if ( !(value instanceof Map) ) {
            return false;
        }
        Map<?, ?> instance = (Map<?, ?>) value;
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( !instance.containsKey(f.getKey()) ) {
                return false;
            }
            Object instanceValueForKey = instance.get(f.getKey());
            if ( instanceValueForKey != null && !f.getValue().isAssignableValue(instanceValueForKey) ) {
                return false;
            }
        }
        return true;
    }
}
