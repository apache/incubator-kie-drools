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
package org.drools.reliability.core;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.Storage;
import org.drools.reliability.core.util.ReliabilityUtils;

public class SerializableStoredRefEvent extends SerializableStoredEvent implements ReferenceWireable {

    private final Map<String, Long> referencedObjects;

    public SerializableStoredRefEvent(Object object, boolean propagated, long timestamp, long duration) {
        super(object, propagated, timestamp, duration);
        referencedObjects = new HashMap<>();
    }

    @Override
    public void addReferencedObject(String fieldName, Long refObjectKey) {
        this.referencedObjects.put(fieldName, refObjectKey);
    }

    @Override
    public StoredObject updateReferencedObjects(Storage<Long, StoredObject> storage) {
        ReliabilityUtils.updateReferencedObjects(storage, this.referencedObjects, this.object);
        return this;
    }
}
