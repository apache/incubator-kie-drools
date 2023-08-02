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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleSerializationReliableRefObjectStore extends SimpleSerializationReliableObjectStore {

    private Map<Class,Long> uniqueObjectTypesInStore;

    public SimpleSerializationReliableRefObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
        uniqueObjectTypesInStore = new HashMap<>();
        if (storage.size()>0){
            storage.values().forEach(sObject -> {this.putIntoObjectTypesList( sObject.getObject() );});
        }
        this.storage = storage.size()>0 ? updateObjectReferences(storage) : storage;
    }

    private Storage<Long, StoredObject> updateObjectReferences(Storage<Long, StoredObject> storage){
        Storage<Long, StoredObject> updateStorage = storage;

        for (Long key: storage.keySet()){
            updateStorage.put(key, ((SerializableStoredRefObject) storage.get(key)).updateReferencedObjects(storage));
        }
        return updateStorage;
    }

    @Override
    public void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = factHandleToStoredObject(handle, reInitPropagated || propagated, object);
        storage.put(getHandleForObject(object).getId(), setReferencedObjects(storedObject));
        // also add the type of the object into the uniqueObjectTypesInStore list (if not already there)
        this.putIntoObjectTypesList(object);
    }

    @Override
    public void removeFromPersistedStorage(Object object) {
        super.removeFromPersistedStorage(object);
        // also remove instance from uniqueObjectTypesInStore
        this.removeFromObjectTypesList(object);
    }

    @Override
    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new SerializableStoredRefObject(object, propagated);
    }

    private StoredObject setReferencedObjects(StoredObject object){
        List<Field> referencedObjects = getReferencedObjects(object.getObject());
        if (referencedObjects.size()>0) {
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
                if (objectKey!=null){
                    ((SerializableStoredRefObject) object).addReferencedObject(field.getName(), objectKey);}
            });
        }
        return object;
    }

    private Long fromObjectToFactHandleId(Object object){
        for (Long key : this.storage.keySet()){
            if (( (SerializableStoredRefObject) storage.get(key)).getObject()==object){
                return key;
            }
        }
        return null;
    }

    private List<Field> getReferencedObjects(Object object){
        Field[] fields = object.getClass().getDeclaredFields();

        List<Field> fieldsWithTypeInTheStore = Arrays.stream(fields)
                .filter(field -> uniqueObjectTypesInStore.containsKey(field.getType()))
                .collect(Collectors.toList());
        return fieldsWithTypeInTheStore;
    }

    private void putIntoObjectTypesList(Object object){
        Long objectTypeCount = uniqueObjectTypesInStore.get(object.getClass());
        if (objectTypeCount!=null){
            uniqueObjectTypesInStore.put(object.getClass(), objectTypeCount+1);
        }else{
            uniqueObjectTypesInStore.put(object.getClass(),Integer.toUnsignedLong(1));
        }
    }

    private void removeFromObjectTypesList(Object object){
        Long objectTypeCount = uniqueObjectTypesInStore.get(object.getClass());
        if (objectTypeCount!=null){
            objectTypeCount--;
            if (objectTypeCount==0){
                uniqueObjectTypesInStore.remove(object.getClass());
            }else {uniqueObjectTypesInStore.put(object.getClass(),objectTypeCount);}
        }
    }

}
