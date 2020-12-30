/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ObjectFilter;

import static java.util.stream.Collectors.toList;

public class IdentityObjectStore implements Externalizable, ObjectStore {

    private Map<Object, InternalFactHandle> fhMap = new IdentityHashMap<>();

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject(fhMap);
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        fhMap = (Map<Object, InternalFactHandle>) in.readObject();
    }

    @Override
    public int size() {
        return fhMap.size();
    }

    @Override
    public boolean isEmpty() {
        return fhMap.isEmpty();
    }

    @Override
    public void clear() {
        fhMap.clear();
    }

    @Override
    public Object getObjectForHandle( InternalFactHandle handle ) {
        InternalFactHandle reconnectedHandle = reconnect(handle);
        return reconnectedHandle != null ? reconnectedHandle.getObject() : null;
    }

    @Override
    public InternalFactHandle reconnect( InternalFactHandle handle ) {
        InternalFactHandle reconnectedHandle = fhMap.values().stream().filter( fh -> fh.getId() == handle.getId() ).findFirst().orElse( null );
        return reconnectedHandle != null && handle.getIdentityHashCode() == reconnectedHandle.getIdentityHashCode() ? reconnectedHandle : null;
    }

    @Override
    public InternalFactHandle getHandleForObject( Object object ) {
        return fhMap.get( object );

    }

    @Override
    public InternalFactHandle getHandleForObjectIdentity( Object object ) {
        return fhMap.get( object );
    }

    @Override
    public void updateHandle( InternalFactHandle handle, Object object ) {
        removeHandle(handle);
        handle.setObject(object);
        addHandle(handle, object);
    }

    @Override
    public void addHandle( InternalFactHandle handle, Object object ) {
        fhMap.put( object, handle );
    }

    @Override
    public void removeHandle( InternalFactHandle handle ) {
        fhMap.remove( handle.getObject() );
    }

    @Override
    public Iterator<Object> iterateObjects() {
        return fhMap.keySet().iterator();
    }

    @Override
    public Iterator<Object> iterateObjects( ObjectFilter filter ) {
        return fhMap.keySet().stream().filter( filter::accept ).iterator();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return fhMap.values().iterator();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles( ObjectFilter filter ) {
        return fhMap.values().stream().filter( fh -> filter.accept( fh.getObject() ) ).iterator();
    }

    @Override
    public Iterator<Object> iterateNegObjects( ObjectFilter filter ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<InternalFactHandle> iterateNegFactHandles( ObjectFilter filter ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FactHandleClassStore getStoreForClass( Class<?> clazz ) {
        return new IdentityFactHandleClassStore( clazz );
    }

    @Override
    public boolean clearClassStore(Class<?> clazz) {
        ObjectFilter filter = new ClassObjectFilter( clazz );
        List<Object> toBeRemoved = fhMap.keySet().stream().filter( filter::accept ).collect( toList() );
        toBeRemoved.forEach( fhMap::remove );
        return !toBeRemoved.isEmpty();
    }

    public class IdentityFactHandleClassStore implements FactHandleClassStore {

        private final Class<?> clazz;

        public IdentityFactHandleClassStore( Class<?> clazz ) {
            this.clazz = clazz;
        }

        @Override
        public Iterator<InternalFactHandle> iterator() {
            ObjectFilter filter = new ClassObjectFilter( clazz );
            return fhMap.values().stream().filter( fh -> filter.accept( fh.getObject() ) ).collect( toList() ).iterator();
        }
    }
}
