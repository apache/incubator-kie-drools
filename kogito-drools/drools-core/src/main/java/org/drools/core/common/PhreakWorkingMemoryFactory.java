/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import java.io.Serializable;

import org.drools.core.SessionConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.Environment;

public class PhreakWorkingMemoryFactory implements WorkingMemoryFactory, Serializable {

    private static final WorkingMemoryFactory INSTANCE = new PhreakWorkingMemoryFactory();

    public static WorkingMemoryFactory getInstance() {
        return INSTANCE;
    }

    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, SessionConfiguration config, Environment environment) {
        InternalWorkingMemory cachedWm = kBase.getCachedSession(config, environment);
        if (cachedWm != null) {
            return cachedWm;
        }

        if (kBase.hasUnits()) {
            throw new IllegalStateException( "Cannot create a KieSession against a KieBase with units" );
        }
        return new StatefulKnowledgeSessionImpl( id, kBase, true, config, environment);
    }

    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, InternalAgenda agenda, Environment environment) {
        if (kBase.hasUnits()) {
            throw new IllegalStateException( "Cannot create a KieSession against a KieBase with units" );
        }
        return new StatefulKnowledgeSessionImpl(id, kBase, handleFactory, propagationContext, config, agenda, environment);
    }
}
