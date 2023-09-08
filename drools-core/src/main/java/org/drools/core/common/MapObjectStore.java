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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ObjectFilter;

import static java.util.stream.Collectors.toList;


public abstract class MapObjectStore implements Externalizable, ObjectStore {

    private Storage<Object, InternalFactHandle> fhStorage;

    protected MapObjectStore(Map<Object, InternalFactHandle> fhMap) {
        this.fhStorage = Storage.fromMap(fhMap);
    }

    protected MapObjectStore(Storage<Object, InternalFactHandle> fhStorage) {
        this.fhStorage = fhStorage;
    }

    @Override
    public void writeExternal(ObjectOutput out ) throws IOException {
        out.writeObject(fhStorage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in ) throws IOException, ClassNotFoundException {
        fhStorage = (Storage<Object, InternalFactHandle>) in.readObject();
    }

    @Override
    public int size() {
        return fhStorage.size();
    }

    @Override
    public boolean isEmpty() {
        return fhStorage.isEmpty();
    }

    @Override
    public void clear() {
        fhStorage.clear();
    }

    @Override
    public Object getObjectForHandle( InternalFactHandle handle ) {
        InternalFactHandle reconnectedHandle = reconnect(handle);
        return reconnectedHandle != null ? reconnectedHandle.getObject() : null;
    }

    @Override
    public InternalFactHandle reconnect( InternalFactHandle handle ) {
        InternalFactHandle reconnectedHandle = fhStorage.values().stream().filter( fh -> fh.getId() == handle.getId() ).findFirst().orElse( null );
        return reconnectedHandle != null && handle.getIdentityHashCode() == reconnectedHandle.getIdentityHashCode() ? reconnectedHandle : null;
    }

    @Override
    public InternalFactHandle getHandleForObject( Object object ) {
        return fhStorage.get( object );

    }

    @Override
    public void updateHandle( InternalFactHandle handle, Object object ) {
        removeHandle(handle);
        handle.setObject(object);
        addHandle(handle, object);
    }

    @Override
    public void addHandle( InternalFactHandle handle, Object object ) {
        fhStorage.put( object, handle );
    }

    @Override
    public void removeHandle( InternalFactHandle handle ) {
        fhStorage.remove( handle.getObject() );
    }

    @Override
    public Iterator<Object> iterateObjects() {
        return fhStorage.keySet().iterator();
    }

    @Override
    public Iterator<Object> iterateObjects(ObjectFilter filter ) {
        return fhStorage.keySet().stream().filter( filter::accept ).iterator();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return fhStorage.values().iterator();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles( ObjectFilter filter ) {
        return fhStorage.values().stream().filter( fh -> filter.accept( fh.getObject() ) ).iterator();
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
        return new MapFactHandleClassStore( clazz );
    }

    @Override
    public boolean clearClassStore(Class<?> clazz) {
        ObjectFilter filter = new ClassObjectFilter( clazz );
        List<Object> toBeRemoved = fhStorage.keySet().stream().filter( filter::accept ).collect( toList() );
        toBeRemoved.forEach( fhStorage::remove );
        return !toBeRemoved.isEmpty();
    }

    public class MapFactHandleClassStore implements FactHandleClassStore {

        private final Class<?> clazz;

        public MapFactHandleClassStore(Class<?> clazz ) {
            this.clazz = clazz;
        }

        @Override
        public Iterator<InternalFactHandle> iterator() {
            ObjectFilter filter = new ClassObjectFilter( clazz );
            return fhStorage.values().stream().filter( fh -> filter.accept( fh.getObject() ) ).collect( toList() ).iterator();
        }
    }
}