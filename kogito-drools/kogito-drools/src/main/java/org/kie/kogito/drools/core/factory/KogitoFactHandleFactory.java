/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.drools.core.factory;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.spi.FactHandleFactory;

public class KogitoFactHandleFactory extends ReteooFactHandleFactory {

    public KogitoFactHandleFactory() {
        super();
    }

    public KogitoFactHandleFactory(long id, long counter) {
        super(id, counter);
    }

    @Override
    protected DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, boolean isTrait) {
        return new KogitoDefaultFactHandle(id, object, recency, entryPoint, isTrait);
    }

    @Override
    protected EventFactHandle createEventFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, boolean isTrait, long timestamp, long duration) {
        return new KogitoEventFactHandle(id, object, recency, timestamp, duration, entryPoint, isTrait);
    }

    @Override
    public FactHandleFactory newInstance() {
        return new KogitoFactHandleFactory();
    }

    @Override
    public FactHandleFactory newInstance(long id, long counter) {
        return new KogitoFactHandleFactory(id, counter);
    }
}
