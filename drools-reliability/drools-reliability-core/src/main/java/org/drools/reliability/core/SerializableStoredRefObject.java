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
