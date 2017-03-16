/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.persistence;

public interface ObjectStoringStrategy {

    /**
     * Similar to ObjectMarshallingStrategy, it is used to 
     * decide whether this implementation is going to work for 
     * the given Object.
     * @param obj a given object
     * @return true if it can persist the given object.
     */
    boolean accept(Object obj);
    
    /**
     * Returns the key for the persisted object.
     * @param persistable the object to persist.
     * @return the key of the persisted object.
     */
    Object persist(Object persistable);
    
    /**
     * Returns the key for the persisted object.
     * @param persistable the object to persist.
     * @return the key of the persisted object.
     */
    Object update(Object persistable);
    
    /**
     * Returns the persisted object.
     * @param key the key of the persisted object.
     * @return a persisted object or null.
     */
    Object read(Object key);
}
