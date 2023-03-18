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

package org.drools.reliability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.infinispan.Cache;
import org.kie.api.runtime.rule.EntryPoint;

public class SimpleReliableObjectStore extends IdentityObjectStore {

    private final Cache<Long, PropagatedObject> cache;

    private boolean reInitPropagated = false;

    public SimpleReliableObjectStore(Cache<Long, PropagatedObject> cache) {
        super();
        this.cache = cache;
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        super.addHandle(handle, object);
        putIntoPersistedCache(object, handle.hasMatches());
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        removeFromPersistedCache(handle.getObject());
        super.removeHandle(handle);
    }

    List<Object> reInit(InternalWorkingMemory session, EntryPoint ep) {
        reInitPropagated = true;
        List<Object> propagated = new ArrayList<>();
        List<Object> notPropagated = new ArrayList<>();
        for (PropagatedObject entry : cache.values()) {
            if (entry.propagated) {
                propagated.add(entry.object);
            } else {
                notPropagated.add(entry.object);
            }
        }
        cache.clear();

        // fact handles with a match have been already propagated in the original session, so they shouldn't fire
        propagated.forEach(ep::insert);
        session.fireAllRules(match -> false);
        reInitPropagated = false;

        // fact handles without any match have never been propagated in the original session, so they should fire
        return notPropagated;
    }

    void putIntoPersistedCache(Object object, boolean propagated) {
        cache.put(fhMap.get(object).getId(), new PropagatedObject(object, reInitPropagated || propagated));
    }

    void removeFromPersistedCache(Object object) {
        InternalFactHandle fh = fhMap.get(object);
        if (fh != null) {
            cache.remove(fh.getId());
        }
    }

    private static class PropagatedObject implements Serializable {
        private final Object object;
        private final boolean propagated;

        private PropagatedObject(Object object, boolean propagated) {
            this.object = object;
            this.propagated = propagated;
        }
    }
}
