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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Storage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSerializationReliableRefObjectStore extends SimpleSerializationReliableObjectStore {

    private Set<String> uniqueObjectTypesInStorage;
    private transient IdentityHashMap<Object, Long> inverseStorage;

    public SimpleSerializationReliableRefObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
        uniqueObjectTypesInStorage = new HashSet<>();
        setInverseStorage(storage);
        if (!storage.isEmpty()) {
            updateObjectTypesList();
            this.storage = updateObjectReferences(storage);
        }
    }

    private void setInverseStorage(Storage<Long, StoredObject> storage){
        inverseStorage = new IdentityHashMap<>();
        storage.keySet().forEach(key -> inverseStorage.put((storage.get(key)).getObject(),key));
    }

    private Storage<Long, StoredObject> updateObjectReferences(Storage<Long, StoredObject> storage) {
        Storage<Long, StoredObject> updateStorage = storage;

        for (Long key : storage.keySet()) {
            updateStorage.put(key, ((ReferenceWireable) storage.get(key)).updateReferencedObjects(storage));
        }
        return updateStorage;
    }

    @Override
    public void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = factHandleToStoredObject(handle, reInitPropagated || propagated, object);
        storage.put(getHandleForObject(object).getId(), setReferencedObjects(storedObject));
        inverseStorage.put(object, getHandleForObject(object).getId());
        // also add the type of the object into the uniqueObjectTypesInStore list (if not already there)
        this.updateObjectTypesList(object);
    }

    @Override
    public void removeFromPersistedStorage(Object object) {
        super.removeFromPersistedStorage(object);
        inverseStorage.remove(object);
        // also remove instance from uniqueObjectTypesInStore
        this.updateObjectTypesList(object);
    }

    @Override
    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new SerializableStoredRefObject(object, propagated);
    }

    @Override
    protected StoredEvent createStoredEvent(boolean propagated, Object object, long timestamp, long duration) {
        return new SerializableStoredRefEvent(object, propagated, timestamp, duration);
    }

    @SuppressWarnings("squid:S3011") // SONAR IGNORE "Make sure that this accessibility update is safe here."
    private StoredObject setReferencedObjects(StoredObject object) {
        List<Field> referencedObjects = getReferencedObjects(object.getObject());
        if (!referencedObjects.isEmpty()) {
            // for each referenced object in sObject
            //  lookup in storage, find the object of reference, get its fact handle id
            //      save this association in the StoredObject
            referencedObjects.forEach(field -> {
                field.setAccessible(true);
                Object fieldObject = null;
                try {
                    fieldObject = field.get(object.getObject());
                } catch (IllegalAccessException e) {
                    throw new ReliabilityRuntimeException(e);
                }
                Long objectKey = fromObjectToFactHandleId(fieldObject);
                if (objectKey != null) {
                    ((ReferenceWireable) object).addReferencedObject(field.getName(), objectKey);
                }
            });
        }
        return object;
    }

    private Long fromObjectToFactHandleId(Object object) {
        return this.inverseStorage.get(object);
    }

    private List<Field> getReferencedObjects(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> uniqueObjectTypesInStorage.contains(field.getType().getName()))
                .collect(Collectors.toList());
    }

    private void updateObjectTypesList(Object object) {
        if (!inverseStorage.keySet().stream().filter(sObject -> sObject.getClass().equals(object.getClass())).findAny().isEmpty()){
            uniqueObjectTypesInStorage.add(object.getClass().getName());
        }else{
            uniqueObjectTypesInStorage.remove(object.getClass().getName());
        }
    }

    private void updateObjectTypesList() {
        // list of unique object types in storage
        uniqueObjectTypesInStorage.clear();
        storage.values().forEach(sObject -> {
            uniqueObjectTypesInStorage.add(sObject.getObject().getClass().getName());
        });
    }
}

