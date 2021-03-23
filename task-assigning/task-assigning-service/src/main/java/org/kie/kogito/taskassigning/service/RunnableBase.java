/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import static org.kie.kogito.taskassigning.service.RunnableBase.Status.DESTROYED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTING;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STOPPED;

public abstract class RunnableBase implements Runnable {

    protected enum Status {
        STARTING,
        STARTED,
        STOPPING,
        STOPPED,
        DESTROYED
    }

    protected final AtomicReference<Status> status = new AtomicReference<>(STOPPED);

    protected final Semaphore startPermit = new Semaphore(0);

    protected void startCheck() {
        if (!status.compareAndSet(STOPPED, STARTING)) {
            throw new IllegalStateException("start method can only be invoked when the status is STOPPED");
        }
    }

    public void destroy() {
        status.set(DESTROYED);
        startPermit.release();
    }

    /**
     * @return true if the destroy() method has been called. False in any other case.
     */
    public boolean isDestroyed() {
        return status.get() == DESTROYED;
    }

    /**
     * The semantic of RunnableBase class is it that can't continue "executing" as soon the destroy() method was invoked
     * or the backing thread was interrupted.
     * 
     * @return true if current RunnableBase can continue executing, false in any other case.
     */
    protected boolean isAlive() {
        return status.get() != DESTROYED && !Thread.currentThread().isInterrupted();
    }
}
