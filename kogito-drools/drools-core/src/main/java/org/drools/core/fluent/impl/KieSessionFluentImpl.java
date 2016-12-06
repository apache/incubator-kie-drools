/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.fluent.impl;

import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.builder.KieSessionFluent;
import org.kie.api.runtime.builder.WorkItemManagerFluent;

import java.util.Map;

public class KieSessionFluentImpl extends BaseBatchFluent<KieSessionFluent, ExecutableBuilder> implements KieSessionFluent {


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
    public WorkItemManagerFluent<WorkItemManagerFluent, KieSessionFluent, ExecutableBuilder> getWorkItemManager() {
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
    public ExecutableBuilder dispose() {
        fluentCtx.addCommand( new DisposeCommand());
        return fluentCtx.getExecutableBuilder();
    }

}
