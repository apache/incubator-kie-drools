/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.executor.impl.event;

import java.io.Externalizable;
import java.util.List;
import java.util.function.Consumer;

import org.jbpm.executor.AsynchronousJobListener;
import org.kie.api.executor.RequestInfo;

/**
 * Interface for ExecutorEventSupportImpl and ExecutorEventSupportEJBImpl so they both can be referenced by
 * one type because the ExecutorEventSupportEJBImpl is no longer a subtype of ExecutorEventSupportImpl since it would
 * violate EJB public methods specification like no final and synchronized public methods. This way, both ExecutorEventSupportImpl
 * and ExecutorEventSupportEJBImpl can be used interchangeably depending on which implementation of ExecutionEventSupport
 * should be used, i.e. plain Java implementation or EJB implementation.
 */
public interface ExecutorEventSupport extends Externalizable {

    void notifyAllListeners(Consumer<AsynchronousJobListener> consumer);

    void addEventListener(AsynchronousJobListener listener);

    void removeEventListener(Class cls);

    void removeEventListener(AsynchronousJobListener listener);

    List<AsynchronousJobListener> getEventListeners();

    int size();

    boolean isEmpty();

    void clear();

    void fireBeforeJobScheduled(RequestInfo job, Throwable exception);

    void fireBeforeJobExecuted(RequestInfo job, Throwable exception);

    void fireBeforeJobCancelled(RequestInfo job, Throwable exception);

    void fireAfterJobScheduled(RequestInfo job, Throwable exception);

    void fireAfterJobExecuted(RequestInfo job, Throwable exception);

    void fireAfterJobCancelled(RequestInfo job, Throwable exception);
}
