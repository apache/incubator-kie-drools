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

package org.drools.reliability.infinispan;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Storage;
import org.drools.reliability.core.SimpleSerializationReliableObjectStore;
import org.drools.reliability.core.StoredObject;

public class SimpleProtoStreamReliableObjectStore extends SimpleSerializationReliableObjectStore {

    public SimpleProtoStreamReliableObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
    }

    @Override
    public void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = handle.isEvent() ?
                new ProtoStreamStoredObject(object, reInitPropagated || propagated, ((EventFactHandle) handle).getStartTimestamp(), ((EventFactHandle) handle).getDuration()) :
                new ProtoStreamStoredObject(object, reInitPropagated || propagated);
        storage.put(getHandleForObject(object).getId(), storedObject);
    }
}
