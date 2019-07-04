/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.services.uow;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.WorkUnit;

/**
 * Simple unit of work that collects work elements
 * throughout the life of the unit and invokes all of them at the end
 * when end method is invoked. It does not invoke the work
 * when abort is invoked, only clears the collected items. 
 *
 */
public class CollectingUnitOfWork implements UnitOfWork {
    
    private Set<WorkUnit> collectedWork;
    private boolean done;

    @Override
    public void start() {
        checkDone();
        if (collectedWork == null) {
            collectedWork = new LinkedHashSet<>();
        }
    }

    @Override
    public void end() {
        checkStarted();
        collectedWork.forEach(work -> work.perform());
        done();
    }

    @Override
    public void abort() {
        checkStarted();
        collectedWork.forEach(work -> work.abort());
        done();
    }

    @Override
    public void intercept(WorkUnit work) {
        checkStarted();
        if (work == null) {
            throw new NullPointerException("Work must be non null");
        }
        collectedWork.add(work);
    }

    
    protected void checkDone() {
        if (done) {
            throw new IllegalStateException("Unit of work is already done (ended or aborted)");
        }
    }
    
    protected void checkStarted() {
        if (collectedWork == null) {
            throw new IllegalStateException("Unit of work is not started");
        }
    }
    
    protected void done() {
        done = true;
        collectedWork = null;
    }
}
