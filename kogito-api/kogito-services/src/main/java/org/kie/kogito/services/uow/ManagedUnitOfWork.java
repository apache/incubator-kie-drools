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

import java.util.function.Consumer;

import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.WorkUnit;

/**
 * Unit of work that is managed with injected consumers
 * to react to various life cycle phases
 *
 */
public class ManagedUnitOfWork implements UnitOfWork {

    private UnitOfWork delegate;
    private Consumer<UnitOfWork> onStart;
    private Consumer<UnitOfWork> onEnd;
    private Consumer<UnitOfWork> onAbort;
        
    public ManagedUnitOfWork(UnitOfWork delegate, Consumer<UnitOfWork> onStart, Consumer<UnitOfWork> onEnd, Consumer<UnitOfWork> onAbort) {
        super();
        this.delegate = delegate;
        this.onStart = onStart;
        this.onEnd = onEnd;
        this.onAbort = onAbort;
    }

    @Override
    public void start() {
        onStart.accept(delegate);
        delegate.start();
    }

    @Override
    public void end() {
        delegate.end();
        onEnd.accept(delegate);
    }

    @Override
    public void abort() {
        delegate.abort();
        onAbort.accept(delegate);
    }

    @Override
    public void intercept(WorkUnit work) {
        delegate.intercept(work);
    }

    public UnitOfWork delegate() {
        return delegate;
    }
}
