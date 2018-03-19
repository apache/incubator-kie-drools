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

package org.jbpm.executor.ejb.impl;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.function.Consumer;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import org.jbpm.executor.AsynchronousJobListener;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.executor.impl.event.ExecutorEventSupportImpl;
import org.kie.api.executor.RequestInfo;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ExecutorEventSupportEJBImpl implements ExecutorEventSupport {

    private ExecutorEventSupportImpl executorEventSupport = new ExecutorEventSupportImpl();

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        executorEventSupport.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        executorEventSupport.writeExternal(out);
    }

    @Override
    public void notifyAllListeners(Consumer<AsynchronousJobListener> consumer) {
        executorEventSupport.notifyAllListeners(consumer);
    }

    @Override
    public void addEventListener(AsynchronousJobListener listener) {
        executorEventSupport.addEventListener(listener);
    }

    @Override
    public void removeEventListener(Class cls) {
        executorEventSupport.removeEventListener(cls);
    }

    @Override
    public void removeEventListener(AsynchronousJobListener listener) {
        executorEventSupport.removeEventListener(listener);
    }

    @Override
    public List<AsynchronousJobListener> getEventListeners() {
        return executorEventSupport.getEventListeners();
    }

    @Override
    public int size() {
        return executorEventSupport.size();
    }

    @Override
    public boolean isEmpty() {
        return executorEventSupport.isEmpty();
    }

    @Override
    public void clear() {
        executorEventSupport.clear();
    }

    @Override
    public void fireBeforeJobScheduled(RequestInfo job, Throwable exception) {
        executorEventSupport.fireBeforeJobScheduled(job, exception);
    }

    @Override
    public void fireBeforeJobExecuted(RequestInfo job, Throwable exception) {
        executorEventSupport.fireBeforeJobExecuted(job, exception);
    }

    @Override
    public void fireBeforeJobCancelled(RequestInfo job, Throwable exception) {
        executorEventSupport.fireBeforeJobCancelled(job, exception);
    }

    @Override
    public void fireAfterJobScheduled(RequestInfo job, Throwable exception) {
        executorEventSupport.fireAfterJobScheduled(job, exception);
    }

    @Override
    public void fireAfterJobExecuted(RequestInfo job, Throwable exception) {
        executorEventSupport.fireAfterJobExecuted(job, exception);
    }

    @Override
    public void fireAfterJobCancelled(RequestInfo job, Throwable exception) {
        executorEventSupport.fireAfterJobCancelled(job, exception);
    }
}
