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
package org.drools.model.prototype.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeFactInstance;

public class HashMapFactImpl implements PrototypeFactInstance, Serializable {

    protected final UUID uuid;

    protected final Prototype prototype;

    protected final Map<String, Object> valuesMap;

    public HashMapFactImpl( Prototype prototype ) {
        this( prototype, new HashMap<>() );
    }

    public HashMapFactImpl( Prototype prototype, Map<String, Object> valuesMap ) {
        this.uuid = UUID.randomUUID();
        this.prototype = prototype;
        this.valuesMap = valuesMap;
    }

    public HashMapFactImpl( UUID uuid, Prototype prototype, Map<String, Object> valuesMap ) {
        this.uuid = uuid;
        this.prototype = prototype;
        this.valuesMap = valuesMap;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Prototype getPrototype() {
        return prototype;
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
    public void put(String name, Object value ) {
        valuesMap.put(name, value);
    }

    @Override
    public Map<String, Object> asMap() {
        return valuesMap;
    }

    @Override
    public String toString() {
        return "Fact " + prototype.getFullName() + " with values = " + valuesMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HashMapFactImpl other = (HashMapFactImpl) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
