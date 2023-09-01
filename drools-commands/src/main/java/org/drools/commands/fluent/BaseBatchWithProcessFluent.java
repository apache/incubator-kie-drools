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
package org.drools.commands.fluent;

import java.util.Map;

import org.kie.internal.builder.fluent.ProcessFluent;
import org.kie.internal.builder.fluent.WorkItemManagerFluent;

public abstract class BaseBatchWithProcessFluent<T, E> extends BaseBatchFluent<T, E>
        implements ProcessFluent<T, E> {

    public BaseBatchWithProcessFluent(ExecutableImpl fluentCtx) {
        super(fluentCtx);
    }

    @Override
    public T startProcess(String processId) {
        return (T) this;
    }

    @Override
    public T startProcess(String processId, Map<String, Object> parameters) {
        return (T) this;
    }

    @Override
    public T createProcessInstance(String processId, Map<String, Object> parameters) {
        return (T) this;
    }

    @Override
    public T startProcessInstance(String processInstanceId) {
        return (T) this;
    }

    @Override
    public T signalEvent(String type, Object event) {
        return (T) this;
    }

    @Override
    public T signalEvent(String type, Object event, String processInstanceId) {
        return (T) this;
    }

    @Override
    public T abortProcessInstance(String processInstanceId) {
        return (T) this;
    }

    @Override
    public WorkItemManagerFluent<WorkItemManagerFluent, T, E> getWorkItemManager() {
        return null;
    }
}