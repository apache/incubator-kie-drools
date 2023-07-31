package org.drools.reliability.core;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Storage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleSerializationReliableRefObjectStore extends SimpleSerializationReliableObjectStore {

    public SimpleSerializationReliableRefObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
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
    }

    @Override
    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new SerializableStoredRefObject(object, propagated);
    }

    private void updateReferencedObjects(StoredObject object){
        List<Field> referencedObjects = getReferencedObjects(object.getObject());
        if (referencedObjects.size()>0) {

        }
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
                    e.printStackTrace();
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

        List<Field> nonPrimitiveFields = Arrays.stream(fields)
                .filter(field -> !field.getType().isPrimitive())
                .filter(field -> !field.getType().equals(String.class))
                .collect(Collectors.toList());
        return nonPrimitiveFields;
    }
}
