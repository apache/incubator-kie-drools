/*
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
package org.drools.core.common;

import java.util.Iterator;

import org.kie.api.internal.utils.KieService;
import org.kie.api.runtime.ObjectFilter;

public interface ObjectStore extends KieService {

    int size();

    boolean isEmpty();
    
    void clear();

    Object getObjectForHandle(InternalFactHandle handle);
    
    InternalFactHandle reconnect(InternalFactHandle factHandle);

    InternalFactHandle getHandleForObject(Object object);

    void updateHandle(InternalFactHandle handle, Object object);

    void addHandle(InternalFactHandle handle, Object object);

    void removeHandle(final InternalFactHandle handle);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<Object> iterateObjects();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<Object> iterateObjects(ObjectFilter filter);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<InternalFactHandle> iterateFactHandles();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<InternalFactHandle> iterateFactHandles(ObjectFilter filter);

    Iterator<Object> iterateNegObjects(ObjectFilter filter);

    Iterator<InternalFactHandle> iterateNegFactHandles(ObjectFilter filter);

    FactHandleClassStore getStoreForClass(Class<?> clazz);

    boolean clearClassStore(Class<?> clazz);

    default Iterator<InternalFactHandle> iterateFactHandles(Class<?> clazz) {
        return getStoreForClass(clazz).iterator();
    }
}
