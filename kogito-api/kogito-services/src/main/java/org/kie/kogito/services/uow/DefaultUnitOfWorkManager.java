/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.uow;

import org.kie.kogito.event.EventManager;
import org.kie.kogito.services.event.impl.BaseEventManager;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkFactory;
import org.kie.kogito.uow.UnitOfWorkManager;

/**
 * Default implementation of the UnitOfWorkManager that is backed by
 * thread local to keep the associated unit of work.
 *
 */
public class DefaultUnitOfWorkManager implements UnitOfWorkManager {
    // uses thread local to associate unit of works to execution context/thread
    private ThreadLocal<UnitOfWork> currentUnitOfWork = new ThreadLocal<>();
    // uses pass through unit of work as fallback if no unit of work has been started
    private UnitOfWork fallbackUnitOfWork = new PassThroughUnitOfWork();
    // factory used to create unit of work 
    private UnitOfWorkFactory factory;
    
    private EventManager eventManager = new BaseEventManager();

    public DefaultUnitOfWorkManager(UnitOfWorkFactory factory) {
        super();
        this.factory = factory;
    }

    public DefaultUnitOfWorkManager(UnitOfWork fallbackUnitOfWork, UnitOfWorkFactory factory) {
        super();
        this.fallbackUnitOfWork = fallbackUnitOfWork;
        this.factory = factory;
    }

    @Override
    public UnitOfWork currentUnitOfWork() {
        UnitOfWork unit = currentUnitOfWork.get();
        
        if (unit == null) {
            return fallbackUnitOfWork;
        }
        return unit;
    }

    @Override
    public UnitOfWork newUnitOfWork() {
        
        return new ManagedUnitOfWork(factory.create(eventManager), this::associate, this::dissociate, this::dissociate);
    }
    
    protected void associate(UnitOfWork unit) {
        currentUnitOfWork.set(unit);
    }

    protected void dissociate(UnitOfWork unit) {
        currentUnitOfWork.set(null);
    }

    @Override
    public EventManager eventManager() {
        return eventManager;
    }
}
