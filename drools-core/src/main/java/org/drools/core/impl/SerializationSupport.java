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

package org.drools.core.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.kie.api.internal.utils.KieService;

/**
 * Support writeExternal/readExternal for drools object serialization.
 * This class is pluggable to tailor serialization depending on a module (e.g. drools-reliability)
 */
public interface SerializationSupport extends KieService {

    boolean supportsExpireJobContext();

    boolean supportsWorkingMemoryReteExpireAction();

    default void registerReteEvaluator(ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    default void unregisterReteEvaluator(ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    default void writeExpireJobContext(ObjectOutput out, ExpireJobContext expireJobContext) throws IOException {
        throw new UnsupportedOperationException();
    }

    default void readExpireJobContext(ObjectInput in, ExpireJobContext expireJobContext) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    default void writeWorkingMemoryReteExpireAction(ObjectOutput out, WorkingMemoryReteExpireAction workingMemoryReteExpireAction) throws IOException {
        throw new UnsupportedOperationException();
    }

    default void readWorkingMemoryReteExpireAction(ObjectInput in, WorkingMemoryReteExpireAction workingMemoryReteExpireAction) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    default void associateDefaultEventHandleForExpiration(long oldHandleId, DefaultEventHandle newDefaultEventHandle) {
        throw new UnsupportedOperationException();
    }

    class Holder {

        private static final SerializationSupport INSTANCE = createInstance();

        private Holder() {}

        static SerializationSupport createInstance() {
            SerializationSupport factory = KieService.load(SerializationSupport.class);
            if (factory == null) {
                return new DefaultSerializationSupport();
            }
            return factory;
        }
    }

    static SerializationSupport get() {
        return SerializationSupport.Holder.INSTANCE;
    }

    class DefaultSerializationSupport implements SerializationSupport {

        @Override
        public boolean supportsWorkingMemoryReteExpireAction() {
            return false;
        }

        @Override
        public boolean supportsExpireJobContext() {
            return false;
        }

    }

}
