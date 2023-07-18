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

import org.drools.core.common.Storage;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SerializableStoredObject extends BaseStoredObject {

    private final Serializable object;
    private final Map<String, Long> referencedObjects;

    public SerializableStoredObject(Object object, boolean propagated) {
        super(propagated);
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("Object must be serializable : " + object.getClass().getCanonicalName());
        }
        this.object = (Serializable) object;
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
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    @Override
    public Serializable getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "SerializableStoredObject{" +
                "object=" + object +
                ", propagated=" + propagated +
                '}';
    }
}
