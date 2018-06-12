package org.drools.core.fluent.impl;

import java.util.Map;

import org.kie.api.runtime.builder.ProcessFluent;
import org.kie.api.runtime.builder.WorkItemManagerFluent;

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
    public T startProcessInstance(long processInstanceId) {
        return (T) this;
    }

    @Override
    public T signalEvent(String type, Object event) {
        return (T) this;
    }

    @Override
    public T signalEvent(String type, Object event, long processInstanceId) {
        return (T) this;
    }

    @Override
    public T abortProcessInstance(long processInstanceId) {
        return (T) this;
    }

    @Override
    public WorkItemManagerFluent<WorkItemManagerFluent, T, E> getWorkItemManager() {
        return null;
    }
}