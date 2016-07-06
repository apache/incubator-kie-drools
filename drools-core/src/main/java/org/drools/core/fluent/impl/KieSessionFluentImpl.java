package org.drools.core.fluent.impl;

import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.internal.fluent.runtime.FluentBuilder;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.fluent.runtime.KieSessionFluent;
import org.kie.internal.fluent.runtime.WorkItemManagerFluent;

import java.util.Map;

public class KieSessionFluentImpl extends BaseBatchFluent<KieSessionFluent, FluentBuilder> implements KieSessionFluent {


    public KieSessionFluentImpl(ExecutableImpl fluentCtx) {
        super(fluentCtx);
    }

    @Override
    public KieSessionFluent startProcess(String processId) {
        return this;
    }

    @Override
    public KieSessionFluent startProcess(String processId, Map<String, Object> parameters) {
        return this;
    }

    @Override
    public KieSessionFluent createProcessInstance(String processId, Map<String, Object> parameters) {
        return this;
    }

    @Override
    public KieSessionFluent startProcessInstance(long processInstanceId) {
        return this;
    }

    @Override
    public KieSessionFluent signalEvent(String type, Object event) {
        return this;
    }

    @Override
    public KieSessionFluent signalEvent(String type, Object event, long processInstanceId) {
        return this;
    }

    @Override
    public KieSessionFluent abortProcessInstance(long processInstanceId) {
        return this;
    }

    @Override
    public WorkItemManagerFluent<WorkItemManagerFluent, KieSessionFluent, FluentBuilder> getWorkItemManager() {
        return null;
    }

    @Override
    public KieSessionFluent fireAllRules() {
        fluentCtx.addCommand( new FireAllRulesCommand());
        return this;
    }

    @Override
    public KieSessionFluent setGlobal(String identifier, Object object) {
        return this;
    }

    @Override
    public KieSessionFluent getGlobal(String identifier) {
        fluentCtx.addCommand(new GetGlobalCommand(identifier));
        return this;
    }

    @Override
    public KieSessionFluent insert(Object object) {
        fluentCtx.addCommand( new InsertObjectCommand(object));
        return this;
    }

    @Override
    public KieSessionFluent update(FactHandle handle, Object object) {
        return this;
    }

    @Override
    public KieSessionFluent delete(FactHandle handle) {
        return this;
    }

    @Override
    public FluentBuilder dispose() {
        fluentCtx.addCommand( new DisposeCommand());
        return fluentCtx.getFluentBuilder();
    }

}
