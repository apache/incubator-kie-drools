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
package org.drools.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public class ObjectStoreWrapper implements Collection {

    public ObjectStore                     store;
    public ObjectFilter                    filter;
    public int                             type;           // 0 == object, 1 == facthandle
    public static final int                OBJECT      = 0;
    public static final int                FACT_HANDLE = 1;

    public ObjectStoreWrapper(ObjectStore store, ObjectFilter filter, int type) {
        this.store = store;
        this.filter = filter;
        this.type = type;
    }

    public boolean contains(Object object) {
        if ( object instanceof FactHandle) {
            return this.store.getObjectForHandle( (InternalFactHandle) object ) != null;
        } else {
            return this.store.getHandleForObject( object ) != null;
        }
    }

    public boolean containsAll(Collection c) {
        for ( Object object : c ) {
            if ( !contains( object ) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        if ( this.filter == null ) {
            return this.store.isEmpty();
        }

        return size() == 0;
    }

    public int size() {
        if ( this.filter == null ) {
            return this.store.size();
        }

        int i = 0;
        for (Object o : this) {
            i++;
        }

        return i;
    }

    public Iterator< ? > iterator() {
        Iterator it;
        if ( type == OBJECT ) {
            if ( filter != null ) {
                it = store.iterateObjects( filter );
            } else {
                it = store.iterateObjects();
            }
        } else {
            if ( filter != null ) {
                it = store.iterateFactHandles( filter );
            } else {
                it = store.iterateFactHandles();
            }
        }
        return it;
    }

    public Object[] toArray() {
        return asList().toArray();
    }

    public Object[] toArray(Object[] array) {
        return asList().toArray(array);
    }

    private List asList() {
        List list = new ArrayList();
        for (Object o : this) {
            list.add(o);
        }
        return list;
    }

    public String toString() {
        Iterator it = iterator();
        if (!it.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            sb.append(it.next());
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public void clear() {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }
}