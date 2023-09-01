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

import org.drools.core.common.Storage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SerializableStoredRefObject extends  SerializableStoredObject {

    private final Map<String, Long> referencedObjects;

    public SerializableStoredRefObject(Object object, boolean propagated) {
        super(object, propagated);
        referencedObjects=new HashMap<>();
    }

    public void addReferencedObject(String fieldName, Long refObjectKey){
        this.referencedObjects.put(fieldName, refObjectKey);
    }

    public StoredObject updateReferencedObjects(Storage<Long, StoredObject> storage){
        this.referencedObjects.keySet().forEach(fieldName -> {
            Optional<Field> refField = Arrays.stream(object.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals(fieldName)).findFirst();
            if (refField.isPresent()){
                refField.get().setAccessible(true);
                try {
                    refField.get().set(this.object, storage.get(this.referencedObjects.get(refField.get().getName())).getObject());
                } catch (IllegalAccessException e) {
                    throw new ReliabilityRuntimeException(e);
                }
            }
        });
        return this;
    }
}
