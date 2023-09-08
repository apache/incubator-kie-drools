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
package org.drools.reliability.core;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.Storage;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.conf.PersistedSessionOption;

import static org.drools.reliability.core.StorageManager.getSessionIdentifier;

public class ReliableStatefulKnowledgeSessionImpl extends StatefulKnowledgeSessionImpl implements ReliableKieSession {

    private transient Storage<String, Object> activationsStorage;

    public ReliableStatefulKnowledgeSessionImpl() {
    }

    public ReliableStatefulKnowledgeSessionImpl(long id,
                                                InternalKnowledgeBase kBase,
                                                boolean initInitFactHandle,
                                                SessionConfiguration config,
                                                Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public ReliableStatefulKnowledgeSessionImpl(long id,
                                                InternalKnowledgeBase kBase,
                                                FactHandleFactory handleFactory,
                                                long propagationContext,
                                                SessionConfiguration config,
                                                Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, environment);
    }

    @Override
    public void dispose() {
        StorageManagerFactory.get().getStorageManager().removeStoragesBySessionId(String.valueOf(getSessionIdentifier(this)));
        super.dispose();
    }

    @Override
    public void startOperation(InternalOperationType operationType) {
        super.startOperation(operationType);
        if (operationType == InternalOperationType.FIRE) {
            ((ReliableGlobalResolver) getGlobalResolver()).updateStorage();
        }

    }

    @Override
    public void endOperation(InternalOperationType operationType) {
        super.endOperation(operationType);
        if (operationType == InternalOperationType.FIRE) {
            ((ReliableGlobalResolver) getGlobalResolver()).updateStorage();
            if (getSessionConfiguration().getPersistedSessionOption().getSafepointStrategy() == PersistedSessionOption.SafepointStrategy.AFTER_FIRE) {
                safepoint();
            }
        }
    }

    @Override
    public Storage<String, Object> getActivationsStorage() {
        return activationsStorage;
    }

    @Override
    public void setActivationsStorage(Storage<String, Object> activationsStorage) {
        this.activationsStorage = activationsStorage;
    }

    @Override
    public void safepoint() {
        getEntryPoints().stream().map(ReliableNamedEntryPoint.class::cast).forEach(ReliableNamedEntryPoint::safepoint);
        if (getSessionConfiguration().getPersistedSessionOption().getActivationStrategy() == PersistedSessionOption.ActivationStrategy.ACTIVATION_KEY
                && activationsStorage.requiresFlush()) {
            activationsStorage.flush();
        }
    }
}
