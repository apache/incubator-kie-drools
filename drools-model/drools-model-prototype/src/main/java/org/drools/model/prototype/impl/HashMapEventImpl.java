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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeEventInstance;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class HashMapEventImpl extends HashMapFactImpl implements PrototypeEventInstance {

    private long timestamp = -1;
    private long expiration = Long.MAX_VALUE;

    public HashMapEventImpl(Prototype prototype) {
        this( prototype, new HashMap<>() );
    }

    public HashMapEventImpl(Prototype prototype, Map<String, Object> valuesMap) {
        super(prototype, valuesMap);
    }

    public HashMapEventImpl(UUID uuid, Prototype prototype, Map<String, Object> valuesMap, long timestamp, long expiration) {
        super(uuid, prototype, valuesMap);
        this.timestamp = timestamp;
        this.expiration = expiration;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public HashMapEventImpl withExpiration(long value, TimeUnit unit) {
        this.expiration = unitToLong( value, unit );
        return this;
    }

    @Override
    public String toString() {
        return "Event " + prototype.getFullName() + " with values = " + valuesMap;
    }
}
