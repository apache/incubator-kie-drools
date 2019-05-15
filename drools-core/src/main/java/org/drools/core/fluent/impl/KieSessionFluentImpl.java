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

import org.drools.core.command.SetActiveAgendaGroup;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieSessionFluent;

public class KieSessionFluentImpl extends BaseBatchWithProcessFluent<KieSessionFluent, ExecutableBuilder> implements KieSessionFluent {

    public KieSessionFluentImpl(ExecutableImpl fluentCtx) {
        super(fluentCtx);
    }

    @Override
    public KieSessionFluent fireAllRules() {
        fluentCtx.addCommand(new FireAllRulesCommand());
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
        fluentCtx.addCommand(new InsertObjectCommand(object));
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
    public KieSessionFluent setActiveRuleFlowGroup(String ruleFlowGroup) {
        return setActiveAgendaGroup(ruleFlowGroup);
    }

    @Override
    public KieSessionFluent setActiveAgendaGroup(String agendaGroup) {
        fluentCtx.addCommand(new SetActiveAgendaGroup(agendaGroup));
        return this;
    }

    @Override
    public ExecutableBuilder dispose() {
        fluentCtx.addCommand(new DisposeCommand());
        return fluentCtx.getExecutableBuilder();
    }
}