/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.ruleunit.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.ruleunit.InternalDataStore;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.DataProcessor;
import org.kie.kogito.rules.impl.DataHandleImpl;

public class ListDataStore<T> implements InternalDataStore<T> {
    private final Map<DataHandle, T> store = new HashMap<>();

    private final Map<String, EntryPointDataProcessor> subscribers = new HashMap<>();

    public DataHandle add(T t) {
        DataHandle dh = new DataHandleImpl();
        store.put(dh, t);
        subscribers.values().forEach( s -> internalInsert( dh, s, t ) );
        return dh;
    }

    @Override
    public void update(DataHandle handle, T object) {
        subscribers.values().forEach( s -> s.update( handle, object ) );
    }

    @Override
    public void remove(DataHandle handle) {
        subscribers.values().forEach( s -> s.delete( handle ) );
        store.remove( handle );
    }

    @Override
    public void subscribe(DataProcessor subscriber) {
        EntryPointDataProcessor processor = (( EntryPointDataProcessor ) subscriber);
        subscribers.put(processor.getId(), processor);
        store.forEach( (dh, t) -> internalInsert( dh, subscriber, t ) );
    }

    @Override
    public void update( FactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation) {
        EntryPointDataProcessor fhProcessor = subscribers.get( (( InternalFactHandle ) fh).getEntryPoint().getEntryPointId() );
        DataHandle dh = (( InternalFactHandle ) fh).getDataHandle();
        subscribers.values().forEach( s -> {
            if ( s == fhProcessor ) {
                s.update( fh, obj, mask, modifiedClass, activation );
            } else {
                s.update( dh, obj, mask, modifiedClass, activation );
            }
        } );
    }

    @Override
    public Iterator<T> iterator() {
        return store.values().iterator();
    }

    private void internalInsert( DataHandle dh, DataProcessor s, T t ) {
        FactHandle fh = s.insert( dh, t );
        (( InternalFactHandle ) fh).setDataStore( this );
        (( InternalFactHandle ) fh).setDataHandle( dh );
    }
}
