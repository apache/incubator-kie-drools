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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private final Map<String, EntryPointDataProcessor> entryPointSubscribers = new HashMap<>();
    private final List<DataProcessor<T>> subscribers = new ArrayList<>();

    public DataHandle add(T t) {
        DataHandle dh = new DataHandleImpl();
        store.put(dh, t);
        entryPointSubscribers.values().forEach( s -> internalInsert( dh, s, t ) );
        subscribers.forEach( s -> internalInsert( dh, s, t ) );
        return dh;
    }

    @Override
    public void update(DataHandle handle, T object) {
        entryPointSubscribers.values().forEach( s -> s.update( handle, object ) );
        subscribers.forEach( s -> s.update( handle, object ) );
    }

    @Override
    public void remove(DataHandle handle) {
        entryPointSubscribers.values().forEach( s -> s.delete( handle ) );
        subscribers.forEach( s -> s.delete( handle ) );
        store.remove( handle );
    }

    @Override
    public void subscribe(DataProcessor processor) {
        if (processor instanceof EntryPointDataProcessor) {
            EntryPointDataProcessor subscriber = (EntryPointDataProcessor) processor;
            entryPointSubscribers.put(subscriber.getId(), subscriber);
        } else {
            subscribers.add(processor);
        }
        store.forEach( (dh, t) -> internalInsert( dh, processor, t ) );
    }

    @Override
    public void update( FactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation) {
        EntryPointDataProcessor fhProcessor = entryPointSubscribers.get( (( InternalFactHandle ) fh).getEntryPoint().getEntryPointId() );
        DataHandle dh = (( InternalFactHandle ) fh).getDataHandle();
        entryPointSubscribers.values().forEach( s -> {
            if ( s == fhProcessor ) {
                s.update( fh, obj, mask, modifiedClass, activation );
            } else {
                s.update( dh, obj, mask, modifiedClass, activation );
            }
        } );
        subscribers.forEach(s -> s.update(dh, (T) obj));
    }

    private void internalInsert( DataHandle dh, DataProcessor s, T t ) {
        FactHandle fh = s.insert( dh, t );
        if (fh != null) {
            (( InternalFactHandle ) fh).setDataStore( this );
            (( InternalFactHandle ) fh).setDataHandle( dh );
        }
    }
}
