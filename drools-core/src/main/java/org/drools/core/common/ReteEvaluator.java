/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.time.TimerService;
import org.kie.api.runtime.Calendars;

public interface ReteEvaluator {

    WorkingMemoryEntryPoint getEntryPoint(String name);

    <T extends Memory> T getNodeMemory(MemoryFactory<T> node);

    InternalKnowledgeBase getKnowledgeBase();

    ActivationsManager getActivationsManager();

    GlobalResolver getGlobalResolver();

    default InternalFactHandle createFactHandle(Object object, ObjectTypeConf conf, WorkingMemoryEntryPoint wmEntryPoint ) {
        return getFactHandleFactory().newFactHandle( object, conf, this, wmEntryPoint );
    }

    FactHandleFactory getFactHandleFactory();

    InternalFactHandle getFactHandle(Object object);

    default EntryPointId getEntryPoint() {
        return EntryPointId.DEFAULT;
    }

    TimerService getTimerService();

    void addPropagation(PropagationEntry propagationEntry);

    default boolean isThreadSafe() {
        return false;
    }

    FactHandleClassStore getStoreForClass(Class<?> classType);

    WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String entryPointId);

    SessionConfiguration getSessionConfiguration();

    RuleEventListenerSupport getRuleEventSupport();

    Calendars getCalendars();

    TimerService getSessionClock();
}
