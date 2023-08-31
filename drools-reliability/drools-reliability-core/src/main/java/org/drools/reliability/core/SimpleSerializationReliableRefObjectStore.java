/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.core;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Storage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleSerializationReliableRefObjectStore extends SimpleSerializationReliableObjectStore {

    private Map<String, Long> uniqueObjectTypesInStore;  // object type name, occurances
    private IdentityHashMap<Object, Long> inverseStorage;

    public SimpleSerializationReliableRefObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
        uniqueObjectTypesInStore = new HashMap<>();
        setInverseStorage(storage);
        /*if (storage.isEmpty()) {
            this.storage = storage; // why is this code here? this is done by the super constructor
        } else {
            updateObjectTypesList();
            this.storage = updateObjectReferences(storage);
        }*/
        if (!storage.isEmpty()) {
            updateObjectTypesList();
            this.storage = updateObjectReferences(storage);
        }
    }

    private void setInverseStorage(Storage<Long, StoredObject> storage){
        inverseStorage = new IdentityHashMap<>();
        storage.keySet().forEach(key -> inverseStorage.put(((StoredObject)storage.get(key)).getObject(),key));
    }

    private Storage<Long, StoredObject> updateObjectReferences(Storage<Long, StoredObject> storage) {
        Storage<Long, StoredObject> updateStorage = storage;

        for (Long key : storage.keySet()) {
            updateStorage.put(key, ((SerializableStoredRefObject) storage.get(key)).updateReferencedObjects(storage));
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
                    ((SerializableStoredRefObject) object).addReferencedObject(field.getName(), objectKey);
                }
            });
        }
        return object;
    }

    private Long fromObjectToFactHandleId(Object object) {
        return this.inverseStorage.get(object);
        /*for (Long key : this.storage.keySet()) {
            if (((SerializableStoredRefObject) storage.get(key)).getObject() == object) {
                return key;
            }
        }
        return null;*/
    }

    private List<Field> getReferencedObjects(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> uniqueObjectTypesInStore.containsKey(field.getType().getName()))
                .collect(Collectors.toList());
    }

    private void updateObjectTypesList(Object object) {
        uniqueObjectTypesInStore.put(object.getClass().getName(),
                storage.values().stream().filter(sObject -> sObject.getObject().getClass().equals(object.getClass())).count());
        // if count==0 then remove entry
        if (uniqueObjectTypesInStore.get(object.getClass().getName()) == 0) {
            uniqueObjectTypesInStore.remove(object.getClass().getName());
        }
    }

    private void updateObjectTypesList() {
        // list of unique object types in storage
        Set<String> uTypeNames = new HashSet<String>();
        storage.values().forEach(sObject -> {
            uTypeNames.add(sObject.getObject().getClass().getName());
        });
        // add unique object types + their occurrences in the uniqueObjectTypesInStore
        uniqueObjectTypesInStore.putAll(storage.values().stream()
                .map(sObject -> sObject.getObject().getClass().getName())
                .filter(uTypeNames::contains)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
    }
}

