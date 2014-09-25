/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.common;

import java.util.Iterator;

import org.drools.core.util.HashTableIterator;
import org.drools.core.util.JavaIteratorAdapter;
import org.kie.api.runtime.rule.FactHandle;

public interface ObjectStore {

    int size();

    boolean isEmpty();
    
    void clear();

    Object getObjectForHandle(FactHandle handle);
    
    InternalFactHandle reconnect(InternalFactHandle factHandle);

    InternalFactHandle getHandleForObject(Object object);
    
    InternalFactHandle getHandleForObjectIdentity(Object object);

    void updateHandle(InternalFactHandle handle,
                                      Object object);

    public abstract void addHandle(InternalFactHandle handle,
                                   Object object);

    public abstract void removeHandle(final InternalFactHandle handle);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects(org.kie.api.runtime.ObjectFilter filter);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles(org.kie.api.runtime.ObjectFilter filter);

    public Iterator iterateNegObjects(org.kie.api.runtime.ObjectFilter filter);

    public Iterator iterateNegFactHandles(org.kie.api.runtime.ObjectFilter filter);

}
